package net.trilogy.arch.commands.architectureUpdate.view;

import net.trilogy.arch.domain.architectureUpdate.Decision;
import net.trilogy.arch.domain.architectureUpdate.Tdd;
import net.trilogy.arch.validation.architectureUpdate.ArchitectureUpdateValidator;
import net.trilogy.arch.validation.architectureUpdate.ArchitectureUpdateValidator.ValidationError;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import java.util.List;

import static net.trilogy.arch.commands.architectureUpdate.view.AuValidateErrorPresenter.PresentationMode.CAPABILITIES_VALIDATION;
import static net.trilogy.arch.commands.architectureUpdate.view.AuValidateErrorPresenter.PresentationMode.FULL_VALIDATION;
import static net.trilogy.arch.commands.architectureUpdate.view.AuValidateErrorPresenter.PresentationMode.TDD_VALIDATION;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class AuValidateErrorPresenterTest {
    @Rule
    public final ErrorCollector collector = new ErrorCollector();

    private ArchitectureUpdateValidator.Results validationResults;

    @Before
    public void setUp() {
        var invalidTddReference = ValidationError.forInvalidTddReference(new Decision.Id("1"), new Tdd.Id("2"));
        var missingTddReference = ValidationError.forMissingTddReference(new Decision.Id("3"));
        var tddsWithoutStories = ValidationError.forTddsWithoutStories(new Tdd.Id("4"));
        validationResults = new ArchitectureUpdateValidator.Results(List.of(invalidTddReference, missingTddReference, tddsWithoutStories));
    }

    @Test
    public void shouldPresentTddErrors() {
        String actual = AuValidateErrorPresenter.present(TDD_VALIDATION, validationResults);
        String expected = "" +
                "Missing TDD:\n" +
                "    Decision \"3\" must have at least one TDD reference.\n" +
                "Invalid TDD Reference:\n" +
                "    Decision \"1\" contains invalid TDD reference \"2\".\n" +
                "";
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void shouldPresentCapabilityErrors() {
        String actual = AuValidateErrorPresenter.present(CAPABILITIES_VALIDATION, validationResults);
        String expected = "" +
                "Missing Capability:\n" +
                "    TDD \"4\" is not referred to by a story.\n" +
                "";
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void shouldPresentAllErrors() {
        String actual = AuValidateErrorPresenter.present(FULL_VALIDATION, validationResults);
        String expected = "" +
                "Missing TDD:\n" +
                "    Decision \"3\" must have at least one TDD reference.\n" +
                "Missing Capability:\n" +
                "    TDD \"4\" is not referred to by a story.\n" +
                "Invalid TDD Reference:\n" +
                "    Decision \"1\" contains invalid TDD reference \"2\".\n" +
                "";
        assertThat(actual, equalTo(expected));
    }
}
