package net.trilogy.arch.adapter.Jira;

import net.trilogy.arch.TestHelper;
import net.trilogy.arch.adapter.FilesFacade;
import net.trilogy.arch.adapter.Jira.JiraStory.InvalidStoryException;
import net.trilogy.arch.adapter.in.ArchitectureDataStructureReader;
import net.trilogy.arch.domain.ArchitectureDataStructure;
import net.trilogy.arch.domain.architectureUpdate.ArchitectureUpdate;
import net.trilogy.arch.domain.architectureUpdate.FunctionalRequirement;
import net.trilogy.arch.domain.architectureUpdate.Tdd;
import net.trilogy.arch.domain.architectureUpdate.TddContainerByComponent;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.fail;

public class JiraStoryTest {

    @Test
    public void ShouldConstructJiraStory() throws Exception {
        // GIVEN:
        var au = getAu();
        var architecture = getArchitecture();
        var featureStory = au.getCapabilityContainer().getFeatureStories().get(0);

        // WHEN:
        final JiraStory actual = new JiraStory(au, architecture, featureStory);

        // THEN:
        final JiraStory expected = new JiraStory(
                "[SAMPLE FEATURE STORY TITLE]",
                List.of(new JiraStory.JiraTdd(new Tdd.Id("[SAMPLE-TDD-ID]"),
                        new Tdd("[SAMPLE TDD TEXT]"),
                        "c4://Internet Banking System/API Application/Reset Password Controller")),
                List.of(new JiraStory.JiraFunctionalRequirement(
                        new FunctionalRequirement.Id("[SAMPLE-REQUIREMENT-ID]"),
                        new FunctionalRequirement("[SAMPLE REQUIREMENT TEXT]",
                                "[SAMPLE REQUIREMENT SOURCE TEXT]",
                                List.of(new Tdd.Id("[SAMPLE-TDD-ID]")))))
        );

        assertThat(actual, equalTo(expected));
    }

    @Test(expected = InvalidStoryException.class)
    public void shouldThrowIfInvalidRequirement() throws Exception {
        // GIVEN
        var au = getAuWithInvalidRequirement();
        var architecture = getArchitecture();
        var featureStory = au.getCapabilityContainer().getFeatureStories().get(0);

        // WHEN:
        new JiraStory(au, architecture, featureStory);

        // THEN raise exception.
    }

    @Test(expected = InvalidStoryException.class)
    public void shouldThrowIfComponentHasNoPath() throws Exception {
        // GIVEN
        var au = getAu();
        ArchitectureDataStructure architecture = getArchitecture();

        architecture.getModel().getComponents().forEach(c -> c.setPath(null));

        var featureStory = au.getCapabilityContainer().getFeatureStories().get(0);

        // WHEN
        final JiraStory actual = new JiraStory(au, architecture, featureStory);

        // THEN
        // Raise Error
    }

    @Test(expected = InvalidStoryException.class)
    public void shouldThrowIfInvalidComponent() throws Exception {
        // GIVEN
        var au = getAuWithInvalidComponent();
        ArchitectureDataStructure architecture = getArchitecture();

        var featureStory = au.getCapabilityContainer().getFeatureStories().get(0);

        // WHEN
        final JiraStory actual = new JiraStory(au, architecture, featureStory);

        // THEN
        // Raise Error
    }

    @Test(expected = InvalidStoryException.class)
    public void shouldThrowIfInvalidTdd() throws Exception {
        // GIVEN
        var au = getAu();
        ArchitectureDataStructure architecture = getArchitecture();

        var featureStory = au.getCapabilityContainer().getFeatureStories().get(0);
        featureStory = featureStory.toBuilder().tddReferences(List.of(new Tdd.Id("Invalid TDD ID"))).build();

        // WHEN
        final JiraStory actual = new JiraStory(au, architecture, featureStory);

        // THEN
        // Raise Error
    }


    private ArchitectureDataStructure getArchitecture() throws Exception {
        return new ArchitectureDataStructureReader(new FilesFacade())
                .load(TestHelper.getPath(getClass(), TestHelper.MANIFEST_PATH_TO_TEST_JIRA_STORY_CREATION).toFile());
    }

    private ArchitectureUpdate getAu() {
        return changeAllTddsToBeUnderComponent("31", ArchitectureUpdate.blank());
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

