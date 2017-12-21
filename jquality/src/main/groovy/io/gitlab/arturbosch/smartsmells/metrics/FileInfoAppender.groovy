package io.gitlab.arturbosch.smartsmells.metrics

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.ConstructorDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.type.ClassOrInterfaceType
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.ast.ClassHelper
import io.gitlab.arturbosch.jpal.ast.MethodHelper
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.jpal.core.CompilationInfo
import io.gitlab.arturbosch.jpal.core.CompilationInfoProcessor
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.common.visitor.InternalVisitor

/**
 * @author Artur Bosch
 */
@CompileStatic
class FileInfoAppender extends InternalVisitor implements CompilationInfoProcessor {

	@Override
	void setup(CompilationInfo info, Resolver resolver) {
		FileInfo fileInfo = new FileInfo(SourcePath.of(info), SourceRange.fromNode(info.unit))
		info.setData(FileInfo.KEY, fileInfo)
	}

	@Override
	void process(CompilationInfo compilationInfo, Resolver resolver) {
		new FileInfoAppender().visit(compilationInfo, resolver)
	}

	@Override
	void cleanup(CompilationInfo info, Resolver resolver) {
	}

	@Override
	void visit(ClassOrInterfaceDeclaration aClass, Resolver arg) {
		def qualifiedType = info.getQualifiedTypeBySimpleName(aClass.nameAsString)
				.orElse(arg.resolveType(new ClassOrInterfaceType(aClass.nameAsString), info))
		def signature = ClassHelper.createFullSignature(aClass)
		def currentClazz = new ClassInfo(qualifiedType, signature, SourcePath.of(info), SourceRange.fromNode(aClass))
		statistics().addClass(currentClazz)
		new MethodInfoAppender(currentClazz).visit(aClass, arg)
		super.visit(aClass, arg)
	}
}

@CompileStatic
class MethodInfoAppender extends InternalVisitor {

	private ClassInfo current

	MethodInfoAppender(ClassInfo current) {
		this.current = current
	}

	@Override
	void visit(ConstructorDeclaration n, Resolver arg) {
		if (ClassHelper.inClassScope(n, current.name)) {
			current.addMethodInfo(MethodInfo.of(n, current))
		}
	}

	@Override
	void visit(MethodDeclaration n, Resolver arg) {
		if (!MethodHelper.isAnonymousMethod(n) && ClassHelper.inClassScope(n, current.name)) {
			current.addMethodInfo(MethodInfo.of(n, current))
		}
	}
}
