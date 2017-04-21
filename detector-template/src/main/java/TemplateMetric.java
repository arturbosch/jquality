import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import io.gitlab.arturbosch.jpal.resolution.Resolver;
import io.gitlab.arturbosch.smartsmells.api.MetricRaiser;
import io.gitlab.arturbosch.smartsmells.metrics.Metric;

/**
 * @author Artur Bosch
 */
public class TemplateMetric implements MetricRaiser {

	private Resolver resolver;

	@Override
	public Metric raise(ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
		return Metric.of("Template", 5);
	}

	@Override
	public void setResolver(Resolver resolver) {
		this.resolver = resolver;
	}

	@Override
	public Resolver getResolver() {
		return resolver;
	}
}
