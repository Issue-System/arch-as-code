package net.trilogy.arch.validation.architectureUpdate;

import net.trilogy.arch.domain.architectureUpdate.Decision;
import net.trilogy.arch.domain.architectureUpdate.Tdd;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import java.util.Set;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;

public class ValidationResultTest {
    @Rule
    public final ErrorCollector collector = new ErrorCollector();

    @Test
    public void shouldBeValid() {
        ValidationResult result = new ValidationResult(Set.of());
        collector.checkThat(result.isValid(), is(true));
        collector.checkThat(result.isValid(ValidationStage.TDD), is(true));
        collector.checkThat(result.isValid(ValidationStage.STORY), is(true));
    }

    @Test
    public void shouldBeInvalid() {
        ValidationResult result = new ValidationResult(Set.of(
                ValidationError.forDecisionsMustHaveTdds(new Decision.Id("ANY")),
                ValidationError.forTddsMustHaveStories(new Tdd.Id("ANY"))
        ));

        collector.checkThat(result.isValid(), is(false));
        collector.checkThat(result.isValid(ValidationStage.TDD), is(false));
        collector.checkThat(result.isValid(ValidationStage.STORY), is(false));
    }

    @Test
    public void shouldBeInvalidForTddErrors() {
        ValidationResult result = new ValidationResult(Set.of(
                ValidationError.forDecisionsMustHaveTdds(new Decision.Id("ANY"))
        ));

        collector.checkThat(result.isValid(), is(false));
        collector.checkThat(result.isValid(ValidationStage.TDD), is(false));

        collector.checkThat(result.isValid(ValidationStage.STORY), is(true));
    }

    @Test
    public void shouldBeInvalidForCapabilityErrors() {
        ValidationResult result = new ValidationResult(Set.of(
                ValidationError.forTddsMustHaveStories(new Tdd.Id("ANY"))
        ));

        collector.checkThat(result.isValid(), is(false));
        collector.checkThat(result.isValid(ValidationStage.STORY), is(false));

        collector.checkThat(result.isValid(ValidationStage.TDD), is(true));
    }

    @Test
    public void shouldGetAllErrors() {
        Set<ValidationError> errors = Set.of(
                ValidationError.forTddsMustHaveStories(Tdd.Id.blank()),
                ValidationError.forTddsMustBeValidReferences(Decision.Id.blank(), Tdd.Id.blank()),
                ValidationError.forDecisionsMustHaveTdds(Decision.Id.blank())
        );

        collector.checkThat(
                new ValidationResult(errors).getErrors(),
                containsInAnyOrder(errors.toArray())
        );
    }

    @Test
    public void shouldGetAllTddErrors() {
        Set<ValidationError> errors = Set.of(
                ValidationError.forTddsMustHaveStories(Tdd.Id.blank()),
                ValidationError.forTddsMustBeValidReferences(Decision.Id.blank(), Tdd.Id.blank()),
                ValidationError.forDecisionsMustHaveTdds(Decision.Id.blank())
        );

        collector.checkThat(
                new ValidationResult(errors).getErrors(ValidationStage.TDD),
                containsInAnyOrder(
                        ValidationError.forTddsMustBeValidReferences(Decision.Id.blank(), Tdd.Id.blank()),
                        ValidationError.forDecisionsMustHaveTdds(Decision.Id.blank())
                )
        );
    }

    @Test
    public void shouldGetAllCapabilityErrors() {
        Set<ValidationError> errors = Set.of(
                ValidationError.forTddsMustHaveStories(Tdd.Id.blank()),
                ValidationError.forTddsMustBeValidReferences(Decision.Id.blank(), Tdd.Id.blank()),
                ValidationError.forDecisionsMustHaveTdds(Decision.Id.blank())
        );

        collector.checkThat(
                new ValidationResult(errors).getErrors(ValidationStage.STORY),
                containsInAnyOrder(
                        ValidationError.forTddsMustHaveStories(Tdd.Id.blank())
                )
        );
    }
}
