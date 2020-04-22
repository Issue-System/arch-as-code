package net.trilogy.arch.validation.architectureUpdate;

import net.trilogy.arch.domain.architectureUpdate.ArchitectureUpdate;
import net.trilogy.arch.domain.architectureUpdate.Decision;
import net.trilogy.arch.domain.architectureUpdate.Tdd;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static net.trilogy.arch.validation.architectureUpdate.ArchitectureUpdateValidator.ErrorType.decisions_must_have_at_least_one_tdd;
import static net.trilogy.arch.validation.architectureUpdate.ArchitectureUpdateValidator.ErrorType.invalid_tdd_reference;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;


public class ArchitectureUpdateValidatorTest_Decisions {
    @Rule
    public final ErrorCollector collector = new ErrorCollector();

    private ArchitectureUpdate invalidAu;

    @Before
    public void setUp() {
        invalidAu = ArchitectureUpdate.builderPreFilledWithBlanks()
                .TDDs(Map.of(Tdd.ComponentReference.blank(), List.of(Tdd.blank())))
                .decisions(Map.of(
                        new Decision.Id("Missing-TDD-Decision-1"), new Decision("Decision-1-text", null),
                        new Decision.Id("Missing-TDD-Decision-2"), new Decision("Decision-2-text", List.of()),
                        new Decision.Id("Bad-TDD-Decision"), new Decision("Decision-2-text", List.of(new Tdd.Id("BAD-TDD-ID"))),
                        new Decision.Id("Valid-Decision"), new Decision("Decision-3-text", List.of(Tdd.Id.blank()))
                )).build();
    }

    @Test
    public void blankAuShouldBeValid() {
        var au = ArchitectureUpdate.blank();

        collector.checkThat(ArchitectureUpdateValidator.validate(au).isValid(), is(true));
    }

    @Test
    public void shouldDetectInvalidAu() {
        var result = ArchitectureUpdateValidator.validate(invalidAu);

        collector.checkThat(result.isValid(), is(false));
    }

    @Test
    public void shouldFindAllBadDecisions() {
        var result = ArchitectureUpdateValidator.validate(invalidAu);
        Set<Decision.Id> invalidElements = result
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
        var result = ArchitectureUpdateValidator.validate(invalidAu);

        collector.checkThat(
                result.getErrorTypesEncountered(),
                containsInAnyOrder(decisions_must_have_at_least_one_tdd, invalid_tdd_reference)
        );
    }

    @Test
    public void shouldFindDecisionsWithMissingTdds() {
        var result = ArchitectureUpdateValidator.validate(invalidAu);
        Set<Decision.Id> invalidElements = result
                .getErrors(decisions_must_have_at_least_one_tdd)
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
        var result = ArchitectureUpdateValidator.validate(invalidAu);

        var errors = result.getErrors(invalid_tdd_reference);
        collector.checkThat(errors.size(), equalTo(1));

        var error = errors.iterator().next();
        collector.checkThat(error.getDescription(), equalTo("Decision \"Bad-TDD-Decision\" contains invalid TDD reference \"BAD-TDD-ID\"."));
        collector.checkThat(error.getElement(), equalTo(new Decision.Id("Bad-TDD-Decision")));
        collector.checkThat(error.getErrorType(), equalTo(invalid_tdd_reference));
    }
}
