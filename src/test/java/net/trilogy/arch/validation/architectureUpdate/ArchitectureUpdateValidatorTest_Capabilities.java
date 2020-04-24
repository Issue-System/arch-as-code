package net.trilogy.arch.validation.architectureUpdate;

import net.trilogy.arch.domain.architectureUpdate.*;
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
import static net.trilogy.arch.validation.architectureUpdate.ArchitectureUpdateValidator.ErrorType.missing_capability;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;

public class ArchitectureUpdateValidatorTest_Capabilities {
    @Rule
    public final ErrorCollector collector = new ErrorCollector();

    private ArchitectureUpdate.ArchitectureUpdateBuilder invalidAu;

    @Before
    public void setUp() {
        invalidAu = ArchitectureUpdate.builderPreFilledWithBlanks()
                .decisions(Map.of())
                .TDDs(
                        Map.of(
                                new Tdd.ComponentReference("Component-0"), List.of(
                                        new Tdd(new Tdd.Id("TDD 0.1"), "text")
                                ),
                                new Tdd.ComponentReference("Component-1"), List.of(
                                        new Tdd(new Tdd.Id("TDD 1.1"), "text"),
                                        new Tdd(new Tdd.Id("TDD 1.2"), "text")
                                ),
                                new Tdd.ComponentReference("Component-2"), List.of(
                                        new Tdd(new Tdd.Id("TDD 2.1"), "text"),
                                        new Tdd(new Tdd.Id("TDD 2.2"), "text")
                                )
                        )
                )
                .capabilityContainer(new CapabilitiesContainer(
                                Epic.blank(),
                                List.of(new FeatureStory("Feat Title", Jira.blank(), List.of(new Tdd.Id("TDD 0.1")), List.of()))
                        )
                );
    }

    @Test
    public void blankAuShouldBeValid() {
        var au = ArchitectureUpdate.blank();

        var result = ArchitectureUpdateValidator.validate(au);

        collector.checkThat(result.isValid(), is(true));
        collector.checkThat(result.isValid(ValidationStage.CAPABILITY), is(true));
        collector.checkThat(result.isValid(ValidationStage.TDD), is(true));
    }

    @Test
    public void shouldDetectInvalidAu() {
        var result = ArchitectureUpdateValidator.validate(invalidAu.build());

        collector.checkThat(result.isValid(), is(false));
        collector.checkThat(result.isValid(ValidationStage.CAPABILITY), is(false));
        collector.checkThat(result.isValid(ValidationStage.TDD), is(true));
    }

    @Test
    public void shouldFindAllErrorTypes() {
        var result = ArchitectureUpdateValidator.validate(invalidAu.build());

        collector.checkThat(
                result.getErrorTypesEncountered(),
                containsInAnyOrder(missing_capability)
        );
    }

    @Test
    public void shouldFindAllErrors() {
        var invalidDecision = Map.of(new Decision.Id("DECISION"), new Decision("DECISION", List.of()));
        ArchitectureUpdate au = invalidAu.decisions(invalidDecision).build();

        var result = ArchitectureUpdateValidator.validate(au);
        Set<EntityReference> invalidElements = result
                .getErrors()
                .stream()
                .map(ArchitectureUpdateValidator.ValidationError::getElement)
                .collect(Collectors.toSet());

        collector.checkThat(
                invalidElements,
                containsInAnyOrder(
                        new Decision.Id("DECISION"),
                        new Tdd.Id("TDD 1.1"),
                        new Tdd.Id("TDD 1.2"),
                        new Tdd.Id("TDD 2.1"),
                        new Tdd.Id("TDD 2.2")
                )
        );
    }

    @Test
    public void shouldFindTDDsWithoutStories() {
        var invalidDecision = Map.of(new Decision.Id("DECISION"), new Decision("DECISION", List.of()));
        ArchitectureUpdate au = invalidAu.decisions(invalidDecision).build();

        var result = ArchitectureUpdateValidator.validate(au);
        Set<EntityReference> invalidElements = result
                .getErrors(missing_capability)
                .stream()
                .map(ArchitectureUpdateValidator.ValidationError::getElement)
                .collect(Collectors.toSet());

        collector.checkThat(
                invalidElements,
                containsInAnyOrder(
                        new Tdd.Id("TDD 1.1"),
                        new Tdd.Id("TDD 1.2"),
                        new Tdd.Id("TDD 2.1"),
                        new Tdd.Id("TDD 2.2")
                )
        );
    }
}
