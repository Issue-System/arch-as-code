package net.trilogy.arch.validation.architectureUpdate;

import net.trilogy.arch.adapter.FilesFacade;
import net.trilogy.arch.adapter.in.ArchitectureDataStructureReader;
import net.trilogy.arch.domain.ArchitectureDataStructure;
import net.trilogy.arch.domain.architectureUpdate.ArchitectureUpdate;
import net.trilogy.arch.domain.architectureUpdate.CapabilitiesContainer;
import net.trilogy.arch.domain.architectureUpdate.Decision;
import net.trilogy.arch.domain.architectureUpdate.Epic;
import net.trilogy.arch.domain.architectureUpdate.FeatureStory;
import net.trilogy.arch.domain.architectureUpdate.FunctionalRequirement;
import net.trilogy.arch.domain.architectureUpdate.Jira;
import net.trilogy.arch.domain.architectureUpdate.Tdd;
import net.trilogy.arch.domain.architectureUpdate.TddContainerByComponent;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static net.trilogy.arch.TestHelper.MANIFEST_PATH_TO_TEST_MODEL_COMPONENTS;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

public class ArchitectureUpdateValidatorTest {
    @Rule
    public final ErrorCollector collector = new ErrorCollector();

    private ArchitectureUpdate invalidAu;
    private ArchitectureDataStructure validDataStructure;

    @Before
    public void setUp() throws IOException {
        validDataStructure = new ArchitectureDataStructureReader(new FilesFacade())
                .load(new File(
                        getClass().getResource(MANIFEST_PATH_TO_TEST_MODEL_COMPONENTS).getPath()
                ));

        invalidAu = ArchitectureUpdate.builderPreFilledWithBlanks()
                .decisions(Map.of(
                        new Decision.Id("Missing-TDD-Decision-1"), new Decision("Decision-1-text", null),
                        new Decision.Id("Missing-TDD-Decision-2"), new Decision("Decision-2-text", List.of()),
                        new Decision.Id("Bad-TDD-Decision"), new Decision("Decision-2-text", List.of(new Tdd.Id("BAD-TDD-ID"))),
                        new Decision.Id("Valid-Decision"), new Decision("Decision-3-text", List.of(new Tdd.Id("Valid-TDD-with-decision-and-story")))
                ))
                .functionalRequirements(
                        Map.of(
                                new FunctionalRequirement.Id("Bad-TDD-Functional-Requirement"),
                                new FunctionalRequirement("Text", "Source", List.of(
                                        new Tdd.Id("BAD-TDD-ID"),
                                        new Tdd.Id("Tdd-with-invalid-component")
                                )),

                                new FunctionalRequirement.Id("Valid-Functional-Requirement"),
                                new FunctionalRequirement("Text", "Source", List.of()),

                                new FunctionalRequirement.Id("Functional-Requirement-Without-Story"),
                                new FunctionalRequirement("Text", "Source", List.of(
                                        new Tdd.Id("Valid-TDD-with-requirement-and-story"),
                                        new Tdd.Id("Valid-TDD-with-requirement-and-story-2"),
                                        new Tdd.Id("Valid-TDD-with-requirement-and-story-3"),
                                        new Tdd.Id("Valid-TDD-with-requirement-and-story-4")
                                ))
                        )
                )
                .tddContainersByComponent(
                        List.of(
                                new TddContainerByComponent(
                                        new Tdd.ComponentReference("Deleted-Component-Id"),
                                        true,
                                        Map.of(new Tdd.Id("Valid-TDD-with-requirement-and-story-2"), new Tdd("text"))
                                ),
                                new TddContainerByComponent(
                                        new Tdd.ComponentReference("38"),
                                        false,
                                        Map.of(new Tdd.Id("Valid-TDD-with-requirement-and-story"), new Tdd("text"))
                                ),
                                new TddContainerByComponent(
                                        new Tdd.ComponentReference("14"),
                                        false,
                                        Map.of(new Tdd.Id("TDD-unused-and-without-story"), new Tdd("text"))
                                ),
                                new TddContainerByComponent(
                                        new Tdd.ComponentReference("15"),
                                        false,
                                        Map.of(
                                                new Tdd.Id("Valid-TDD-with-decision-and-story"), new Tdd("text"),
                                                new Tdd.Id("TDD-unused-with-story"), new Tdd("text")
                                        )
                                ),
                                new TddContainerByComponent(
                                        new Tdd.ComponentReference("Invalid-Component-Id"),
                                        false,
                                        Map.of(
                                                new Tdd.Id("Tdd-with-invalid-component"), new Tdd("text"),
                                                new Tdd.Id("Valid-TDD-with-requirement-and-story"), new Tdd("INVALID BECAUSE DUPLICATED ID")
                                        )
                                ),
                                new TddContainerByComponent(
                                        new Tdd.ComponentReference("Invalid-Component-Id-2"),
                                        null,
                                        Map.of(new Tdd.Id("Valid-TDD-with-requirement-and-story-3"), new Tdd("text"))
                                ),
                                new TddContainerByComponent(
                                        new Tdd.ComponentReference("16"), 
                                        true, // ERR: not actually deleted
                                        Map.of(new Tdd.Id("Valid-TDD-with-requirement-and-story-4"), new Tdd("text"))
                                ),
                                new TddContainerByComponent(
                                        new Tdd.ComponentReference("38"), // ERR: duplicated
                                        false,
                                        Map.of()
                                )
                        )
                )
                .capabilityContainer(
                        new CapabilitiesContainer(
                                Epic.blank(),
                                List.of(
                                        new FeatureStory("Feat Title", Jira.blank(), List.of(
                                                new Tdd.Id("Valid-TDD-with-requirement-and-story"),
                                                new Tdd.Id("Valid-TDD-with-requirement-and-story-2"),
                                                new Tdd.Id("Valid-TDD-with-requirement-and-story-3"),
                                                new Tdd.Id("Valid-TDD-with-requirement-and-story-4"),
                                                new Tdd.Id("Invalid-TDD-ID"),
                                                new Tdd.Id("Tdd-with-invalid-component")
                                        ), List.of(
                                                new FunctionalRequirement.Id("Valid-Functional-Requirement")
                                        )),

                                        new FeatureStory("Feat Title 2", Jira.blank(), List.of(
                                                new Tdd.Id("Valid-TDD-with-decision-and-story"),
                                                new Tdd.Id("TDD-unused-with-story")
                                        ), List.of(
                                                new FunctionalRequirement.Id("Bad-TDD-Functional-Requirement"),
                                                new FunctionalRequirement.Id("Bad-Functional-Requirement-ID")
                                        )),

                                        new FeatureStory("Feat Title 3", Jira.blank(), List.of(
                                                new Tdd.Id("Valid-TDD-with-decision-and-story")
                                        ), List.of()),

                                        new FeatureStory("Feat Title 4", Jira.blank(), List.of(), List.of(
                                                new FunctionalRequirement.Id("Valid-Functional-Requirement")
                                        ))
                                )
                        )
                )
                .build();
    }

    @Test
    public void blankAuShouldBeValid() {
        var result = ArchitectureUpdateValidator.validate(ArchitectureUpdate.blank(), validDataStructure);

        collector.checkThat(result.isValid(), is(true));
        collector.checkThat(result.isValid(ValidationStage.STORY), is(true));
        collector.checkThat(result.isValid(ValidationStage.TDD), is(true));
    }

    @Test
    public void shouldFindAllErrors() {
        var actualErrors = ArchitectureUpdateValidator.validate(invalidAu, validDataStructure).getErrors();

        var expectedErrors = List.of(
                ValidationError.forTddsMustBeValidReferences(new Decision.Id("Bad-TDD-Decision"), new Tdd.Id("BAD-TDD-ID")),
                ValidationError.forTddsMustBeValidReferences(new FunctionalRequirement.Id("Bad-TDD-Functional-Requirement"), new Tdd.Id("BAD-TDD-ID")),

                ValidationError.forDecisionsMustHaveTdds(new Decision.Id("Missing-TDD-Decision-1")),
                ValidationError.forDecisionsMustHaveTdds(new Decision.Id("Missing-TDD-Decision-2")),

                ValidationError.forMustHaveStories(new Tdd.Id("TDD-unused-and-without-story")),
                ValidationError.forMustHaveStories(new FunctionalRequirement.Id("Functional-Requirement-Without-Story")),

                ValidationError.forTddsMustHaveDecisionsOrRequirements(new Tdd.Id("TDD-unused-and-without-story")),
                ValidationError.forTddsMustHaveDecisionsOrRequirements(new Tdd.Id("TDD-unused-with-story")),

                ValidationError.forStoriesTddsMustBeValidReferences(new Tdd.Id("Invalid-TDD-ID"), "Feat Title"),

                ValidationError.forTddsComponentsMustBeValidReferences(new Tdd.ComponentReference("Invalid-Component-Id")),
                ValidationError.forTddsComponentsMustBeValidReferences(new Tdd.ComponentReference("Invalid-Component-Id-2")),

                ValidationError.forTddsComponentsMustBeDeletedIfMarked(new Tdd.ComponentReference("16")),

                ValidationError.forFunctionalRequirementsMustBeValidReferences("Feat Title 2", new FunctionalRequirement.Id("Bad-Functional-Requirement-ID")),

                ValidationError.forStoriesMustHaveTdds("Feat Title 4"),

                ValidationError.forStoriesMustHaveFunctionalRequirements("Feat Title 3"),

                ValidationError.forDuplicatedTdd(new Tdd.Id("Valid-TDD-with-requirement-and-story")),

                ValidationError.forDuplicatedComponent(new Tdd.ComponentReference("38"))
        );

        expectedErrors.forEach(e ->
                collector.checkThat(actualErrors, hasItem(e)));

        collector.checkThat(actualErrors.size(), equalTo(expectedErrors.size()));
    }
}
