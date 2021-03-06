package net.trilogy.arch.adapter.jira;

import net.trilogy.arch.TestHelper;
import net.trilogy.arch.adapter.architectureYaml.ArchitectureDataStructureObjectMapper;
import net.trilogy.arch.adapter.jira.JiraStory.InvalidStoryException;
import net.trilogy.arch.domain.ArchitectureDataStructure;
import net.trilogy.arch.domain.architectureUpdate.*;
import net.trilogy.arch.facade.FilesFacade;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class JiraStoryTest {

    @Test
    public void ShouldConstructJiraStory() throws Exception {
        // GIVEN:
        var au = getAu();
        var afterAuArchitecture = getArchitectureAfterAu();
        var beforeAuArchitecture = getArchitectureBeforeAu();
        var featureStory = au.getCapabilityContainer().getFeatureStories().get(0);

        // WHEN:
        final JiraStory actual = new JiraStory(au, beforeAuArchitecture, afterAuArchitecture, featureStory);

        // THEN:
        final JiraStory expected = new JiraStory(
                "story title",
                List.of(
                    new JiraStory.JiraTdd(
                        new Tdd.Id("TDD 1"),
                        new Tdd("TDD 1 text"),
                        "c4://Internet Banking System/API Application/Reset Password Controller"
                    ),
                    new JiraStory.JiraTdd(
                        new Tdd.Id("TDD 3"),
                        new Tdd("TDD 3 text"),
                        "c4://Internet Banking System/API Application/Sign In Controller" // deleted component id: 29
                    )
                ),
                List.of(
                    new JiraStory.JiraFunctionalRequirement(
                        new FunctionalRequirement.Id("[SAMPLE-REQUIREMENT-ID]"),
                        new FunctionalRequirement(
                            "[SAMPLE REQUIREMENT TEXT]",
                            "[SAMPLE REQUIREMENT SOURCE TEXT]",
                            List.of(new Tdd.Id("[SAMPLE-TDD-ID]"))
                        )
                    )
                )
        );

        assertThat(actual, equalTo(expected));
    }

    @Test(expected = InvalidStoryException.class)
    public void shouldThrowIfInvalidRequirement() throws Exception {
        // GIVEN
        var au = getAuWithInvalidRequirement();
        var featureStory = au.getCapabilityContainer().getFeatureStories().get(0);

        // WHEN:
        new JiraStory(au, getArchitectureBeforeAu(), getArchitectureAfterAu(), featureStory);

        // THEN raise exception.
    }

    @Test(expected = InvalidStoryException.class)
    public void shouldThrowIfComponentHasNoPath() throws Exception {
        // GIVEN
        var au = getAu();
        ArchitectureDataStructure architectureAfterAu = getArchitectureAfterAu();

        architectureAfterAu.getModel().getComponents().forEach(c -> c.setPath((String) null));

        var featureStory = au.getCapabilityContainer().getFeatureStories().get(0);

        // WHEN
        new JiraStory(au, getArchitectureBeforeAu(), architectureAfterAu, featureStory);

        // THEN
        // Raise Error
    }

    @Test(expected = InvalidStoryException.class)
    public void shouldThrowIfInvalidComponent() throws Exception {
        // GIVEN
        var au = getAuWithInvalidComponent();

        var featureStory = au.getCapabilityContainer().getFeatureStories().get(0);

        // WHEN
        new JiraStory(au, getArchitectureBeforeAu(), getArchitectureAfterAu(), featureStory);

        // THEN
        // Raise Error
    }

    @Test(expected = InvalidStoryException.class)
    public void shouldThrowIfInvalidTdd() throws Exception {
        // GIVEN
        var au = getAu();

        var featureStory = au.getCapabilityContainer().getFeatureStories().get(0);
        featureStory = featureStory.toBuilder().tddReferences(List.of(new Tdd.Id("Invalid TDD ID"))).build();

        // WHEN
        new JiraStory(au, getArchitectureBeforeAu(), getArchitectureAfterAu(), featureStory);

        // THEN
        // Raise Error
    }


    private ArchitectureDataStructure getArchitectureBeforeAu() throws Exception {
        final String archAsString = new FilesFacade().readString(TestHelper.getPath(
                getClass(),
                TestHelper.MANIFEST_PATH_TO_TEST_JIRA_STORY_CREATION).toFile().toPath());
        return new ArchitectureDataStructureObjectMapper().readValue(archAsString.replaceAll("29", "404"));
    }

    private ArchitectureDataStructure getArchitectureAfterAu() throws Exception {
        final String archAsString = new FilesFacade().readString(TestHelper.getPath(
                getClass(),
                TestHelper.MANIFEST_PATH_TO_TEST_JIRA_STORY_CREATION).toFile().toPath());
        return new ArchitectureDataStructureObjectMapper().readValue(archAsString);
    }

    private ArchitectureUpdate getAu() {
        return ArchitectureUpdate.builderPreFilledWithBlanks()
            .tddContainersByComponent(List.of(
                new TddContainerByComponent(
                    new Tdd.ComponentReference("31"),
                    false,
                    Map.of(
                        new Tdd.Id("TDD 1"), new Tdd("TDD 1 text"),
                        new Tdd.Id("TDD 2"), new Tdd("TDD 2 text"),
                        new Tdd.Id("[SAMPLE-TDD-ID]"), new Tdd("sample tdd text")
                    )
                ),
                new TddContainerByComponent(
                    new Tdd.ComponentReference("404"),
                    true,
                    Map.of(
                        new Tdd.Id("TDD 3"), new Tdd("TDD 3 text"),
                        new Tdd.Id("TDD 4"), new Tdd("TDD 4 text")
                    )
                )
            ))
            .capabilityContainer(new CapabilitiesContainer(
                    Epic.blank(), 
                    List.of(
                        new FeatureStory(
                            "story title", 
                            new Jira("", ""), 
                            List.of(new Tdd.Id("TDD 1"), new Tdd.Id("TDD 3")), 
                            List.of(FunctionalRequirement.Id.blank()))
                    )
                )
            )
            .build();
    }

    private ArchitectureUpdate getAuWithInvalidComponent() {
        return changeAllTddsToBeUnderComponent("1231231323123", getAu());
    }

    private ArchitectureUpdate getAuWithInvalidRequirement() {
        return getAu().toBuilder().functionalRequirements(
                Map.of(new FunctionalRequirement.Id("different id than the reference in the story"),
                        new FunctionalRequirement("any text", "any source", List.of())))
                .build();
    }

    private ArchitectureUpdate changeAllTddsToBeUnderComponent(String newComponentId, ArchitectureUpdate au) {
        var oldTdds = new HashMap<Tdd.Id, Tdd>();
        for (var container : au.getTddContainersByComponent()) {
            oldTdds.putAll(container.getTdds());
        }
        final TddContainerByComponent newComponentWithTdds = new TddContainerByComponent(
                new Tdd.ComponentReference(newComponentId),
                false,
                oldTdds
        );
        return au.toBuilder().tddContainersByComponent(List.of(newComponentWithTdds)).build();
    }
}
