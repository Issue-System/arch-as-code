package net.trilogy.arch.validation.architectureUpdate;

import net.trilogy.arch.domain.architectureUpdate.ArchitectureUpdate;
import net.trilogy.arch.domain.architectureUpdate.Decision;
import net.trilogy.arch.domain.architectureUpdate.Tdd;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import java.util.List;
import java.util.Map;

import static net.trilogy.arch.validation.architectureUpdate.ArchitectureUpdateValidator.ErrorType.decisions_must_have_at_least_one_tdd;
import static net.trilogy.arch.validation.architectureUpdate.ArchitectureUpdateValidator.ErrorType.invalid_tdd_reference;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;


public class ArchitectureUpdateValidatorTest {
    @Rule
    public final ErrorCollector collector = new ErrorCollector();

    @Test
    public void blankAuShouldBeValid() {
        var au = ArchitectureUpdate.blank();

        collector.checkThat(ArchitectureUpdateValidator.validate(au).isValid(), is(true));
    }

    @Test
    public void shouldValidateDecisions() {
        var au = ArchitectureUpdate.builderPreFilledWithBlanks()
                .TDDs(Map.of(Tdd.ComponentReference.blank(), List.of(Tdd.blank())))
                .decisions(Map.of(
                        new Decision.Id("Missing-TDD-Decision-1"), new Decision("Decision-1-text", List.of()),
                        new Decision.Id("Missing-TDD-Decision-2"), new Decision("Decision-2-text", List.of()),
                        new Decision.Id("Bad-TDD-Decision"), new Decision("Decision-2-text", List.of(new Tdd.Id("BAD-TDD-ID"))),
                        new Decision.Id("Valid-Decision"), new Decision("Decision-3-text", List.of(Tdd.Id.blank()))
                )).build();

        var validatedResults = ArchitectureUpdateValidator.validate(au);

        collector.checkThat(validatedResults.isValid(), is(false));
        collector.checkThat(validatedResults.getIds(), containsInAnyOrder(
                new Decision.Id("Missing-TDD-Decision-1"),
                new Decision.Id("Missing-TDD-Decision-2"),
                new Decision.Id("Bad-TDD-Decision"))
        );
        collector.checkThat(validatedResults.getIds(decisions_must_have_at_least_one_tdd), containsInAnyOrder(
                new Decision.Id("Missing-TDD-Decision-1"),
                new Decision.Id("Missing-TDD-Decision-2"))
        );
        collector.checkThat(validatedResults.getIds(invalid_tdd_reference), containsInAnyOrder(new Decision.Id("Bad-TDD-Decision")));
        collector.checkThat(validatedResults.getErrors(), containsInAnyOrder(decisions_must_have_at_least_one_tdd,
                invalid_tdd_reference));
    }

}
