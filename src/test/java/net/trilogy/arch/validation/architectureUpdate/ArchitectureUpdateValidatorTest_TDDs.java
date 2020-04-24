package net.trilogy.arch.validation.architectureUpdate;

import net.trilogy.arch.domain.architectureUpdate.ArchitectureUpdate;
import net.trilogy.arch.domain.architectureUpdate.CapabilitiesContainer;
import net.trilogy.arch.domain.architectureUpdate.Decision;
import net.trilogy.arch.domain.architectureUpdate.EntityReference;
import net.trilogy.arch.domain.architectureUpdate.Epic;
import net.trilogy.arch.domain.architectureUpdate.FeatureStory;
import net.trilogy.arch.domain.architectureUpdate.Jira;
import net.trilogy.arch.domain.architectureUpdate.Tdd;
import net.trilogy.arch.validation.architectureUpdate.ArchitectureUpdateValidator.ValidationStage;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static net.trilogy.arch.validation.architectureUpdate.ArchitectureUpdateValidator.ErrorType.missing_tdd;
import static net.trilogy.arch.validation.architectureUpdate.ArchitectureUpdateValidator.ErrorType.invalid_tdd_reference;
import static org.hamcrest.Matchers.*;


public class ArchitectureUpdateValidatorTest_TDDs {
    @Rule
    public final ErrorCollector collector = new ErrorCollector();

    private ArchitectureUpdate.ArchitectureUpdateBuilder invalidAu;

    @Before
    public void setUp() {
        invalidAu = ArchitectureUpdate.builderPreFilledWithBlanks()
                .decisions(Map.of(
                        new Decision.Id("Missing-TDD-Decision-1"), new Decision("Decision-1-text", null),
                        new Decision.Id("Missing-TDD-Decision-2"), new Decision("Decision-2-text", List.of()),
                        new Decision.Id("Bad-TDD-Decision"), new Decision("Decision-2-text", List.of(new Tdd.Id("BAD-TDD-ID"))),
                        new Decision.Id("Valid-Decision"), new Decision("Decision-3-text", List.of(Tdd.Id.blank()))
                ));
    }

    @Test
    public void blankAuShouldBeValid() {
        var au = ArchitectureUpdate.blank();

        ArchitectureUpdateValidator.Results result = ArchitectureUpdateValidator.validate(au);

        collector.checkThat(result.isValid(), is(true));
        collector.checkThat(result.isValid(ValidationStage.TDD), is(true));
        collector.checkThat(result.isValid(ValidationStage.CAPABILITY), is(true));
    }

    @Test
    public void shouldDetectInvalidAu() {
        var result = ArchitectureUpdateValidator.validate(invalidAu.build());

        collector.checkThat(result.isValid(), is(false));
        collector.checkThat(result.isValid(ValidationStage.TDD), is(false));
        collector.checkThat(result.isValid(ValidationStage.CAPABILITY), is(true));
    }

    @Test
    public void shouldFindAllErrors() {
        var result = ArchitectureUpdateValidator.validate(invalidAu.build());
        Set<EntityReference> invalidElements = result
                .getErrors()
                .stream()
                .map(ArchitectureUpdateValidator.ValidationError::getElement)
                .collect(Collectors.toSet());

        collector.checkThat(
                invalidElements,
                containsInAnyOrder(
                        new Decision.Id("Missing-TDD-Decision-1"),
                        new Decision.Id("Missing-TDD-Decision-2"),
                        new Decision.Id("Bad-TDD-Decision")
                )
        );
    }

    @Test
    public void shouldFindAllErrorTypes() {
        var result = ArchitectureUpdateValidator.validate(invalidAu.build());

        collector.checkThat(
                result.getErrorTypesEncountered(),
                containsInAnyOrder(missing_tdd, invalid_tdd_reference)
        );
    }

    @Test
    public void shouldFindDecisionsWithMissingTdds() {
        var result = ArchitectureUpdateValidator.validate(invalidAu.build());
        Set<EntityReference> invalidElements = result
                .getErrors(missing_tdd)
                .stream()
                .map(ArchitectureUpdateValidator.ValidationError::getElement)
                .collect(Collectors.toSet());

        collector.checkThat(
                invalidElements,
                containsInAnyOrder(
                        new Decision.Id("Missing-TDD-Decision-1"),
                        new Decision.Id("Missing-TDD-Decision-2")
                )
        );
    }

    @Test
    public void shouldFindDecisionsWithBadTdds() {
        var result = ArchitectureUpdateValidator.validate(invalidAu.build());

        var errors = result.getErrors(invalid_tdd_reference);
        collector.checkThat(errors.size(), equalTo(1));

        var error = errors.iterator().next();
        collector.checkThat(error.getDescription(), equalTo("Decision \"Bad-TDD-Decision\" contains invalid TDD reference \"BAD-TDD-ID\"."));
        collector.checkThat(error.getElement(), equalTo(new Decision.Id("Bad-TDD-Decision")));
        collector.checkThat(error.getErrorType(), equalTo(invalid_tdd_reference));
    }
}
