import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import io.gitlab.arturbosch.jpal.resolution.Resolver;
import io.gitlab.arturbosch.smartsmells.common.Visitor;

/**
 * @author Artur Bosch
 */
public class TemplateVisitor extends Visitor<TemplateSmell> {

	@SuppressWarnings({"FieldCanBeLocal", "unused"})
	private final String customProperty;

	public TemplateVisitor(String customProperty) {
		this.customProperty = customProperty;
	}

	@Override
	public void visit(ClassOrInterfaceDeclaration n, Resolver arg) {
		System.out.println("Visiting class: " + n.getNameAsString() + " from TemplateVisitor!");
	}
}
