package com.gitlab.artismarti.smartsmells.common

import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.PackageDeclaration

/**
 * @author artur
 */
class PackageImportHolder {

	String packageName
	Map<String, String> imports

	PackageImportHolder(PackageDeclaration packageDeclaration, List<ImportDeclaration> imports) {
		this.packageName = Optional.ofNullable(packageDeclaration).map { it.packageName }.orElse("")
		this.imports = imports.collectEntries {
			[Arrays.asList(it.toStringWithoutComments().split("\\.")).last(), it.name.toStringWithoutComments()]
		}
	}

}
