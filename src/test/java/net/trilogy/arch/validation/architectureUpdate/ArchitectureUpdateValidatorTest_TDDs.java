package net.trilogy.arch.validation.architectureUpdate;

import net.trilogy.arch.domain.architectureUpdate.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static net.trilogy.arch.validation.architectureUpdate.ArchitectureUpdateValidator.ErrorType.decisions_must_have_at_least_one_tdd;
import static net.trilogy.arch.validation.architectureUpdate.ArchitectureUpdateValidator.ErrorType.tdd_must_have_story;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;

public class ArchitectureUpdateValidatorTest_TDDs {
    @Rule
    public final ErrorCollector collector = new ErrorCollector();

    private ArchitectureUpdate invalidAu;

    @Before
    public void setUp() {
        invalidAu = ArchitectureUpdate.builderPreFilledWithBlanks()
                .decisions(Map.of(
                        new Decision.Id("Decision-1"), new Decision("text", List.of())
                ))
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
                )
                .build();
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
    public void shouldFindAllErrors() {
        var result = ArchitectureUpdateValidator.validate(invalidAu);
        Set<EntityReference> invalidElements = result
                .getErrors()
                .stream()
                .map(ArchitectureUpdateValidator.ValidationError::getElement)
                .collect(Collectors.toSet());

        collector.checkThat(
                invalidElements,
                containsInAnyOrder(
                        new Decision.Id("Decision-1"),
                        new Tdd.Id("TDD 1.1"),
                        new Tdd.Id("TDD 1.2"),
                        new Tdd.Id("TDD 2.1"),
                        new Tdd.Id("TDD 2.2")
                )
        );
    }

    @Test
    public void shouldFindAllErrorTypes() {
        var result = ArchitectureUpdateValidator.validate(invalidAu);

        collector.checkThat(
                result.getErrorTypesEncountered(),
                containsInAnyOrder(tdd_must_have_story, decisions_must_have_at_least_one_tdd)
        );
    }


    @Test
    public void shouldFindTDDsWithoutStories() {
        var result = ArchitectureUpdateValidator.validate(invalidAu);
        Set<EntityReference> invalidElements = result
                .getErrors(tdd_must_have_story)
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
