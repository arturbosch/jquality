import io.gitlab.arturbosch.smartsmells.api.Detector;
import io.gitlab.arturbosch.smartsmells.common.Visitor;
import io.gitlab.arturbosch.smartsmells.config.Smell;

/**
 * @author Artur Bosch
 */
public class TemplateDetector extends Detector<TemplateSmell> {

	@Override
	protected Visitor getVisitor() {
		return new TemplateVisitor();
	}

	@Override
	public Smell getType() {
		return Smell.UNKNOWN;
	}

}
