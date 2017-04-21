import io.gitlab.arturbosch.smartsmells.api.Detector;
import io.gitlab.arturbosch.smartsmells.common.Visitor;

/**
 * @author Artur Bosch
 */
public class TemplateDetector extends Detector<TemplateSmell> {

	@Override
	public String getId() {
		return "template";
	}

	@Override
	protected Visitor getVisitor() {
		return new TemplateVisitor(valueOfConfigKey("my_property"));
	}

}
