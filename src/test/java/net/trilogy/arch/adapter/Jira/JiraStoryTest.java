package net.trilogy.arch.adapter.Jira;

import net.trilogy.arch.TestHelper;
import net.trilogy.arch.adapter.FilesFacade;
import net.trilogy.arch.adapter.Jira.JiraStory;
import net.trilogy.arch.adapter.Jira.JiraStory.InvalidStoryException;
import net.trilogy.arch.adapter.in.ArchitectureDataStructureReader;
import net.trilogy.arch.domain.ArchitectureDataStructure;
import net.trilogy.arch.domain.architectureUpdate.ArchitectureUpdate;
import net.trilogy.arch.domain.architectureUpdate.FunctionalRequirement;
import net.trilogy.arch.domain.architectureUpdate.Tdd;
import org.junit.Ignore;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.fail;

public class JiraStoryTest {

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

    private ArchitectureDataStructure getArchitecture() throws Exception {
        return new ArchitectureDataStructureReader(new FilesFacade())
                .load(TestHelper.getPath(getClass(), TestHelper.MANIFEST_PATH_TO_TEST_JIRA_STORY_CREATION).toFile());
    }

    private ArchitectureUpdate getAu() {
        return changeAllTddsToBeUnderComponent("Component-31", ArchitectureUpdate.blank());
    }

    private ArchitectureUpdate getAuWithInvalidRequirement() {
        return getAu().toBuilder().functionalRequirements(
                Map.of(new FunctionalRequirement.Id("different id than the reference in the story"),
                        new FunctionalRequirement("any text", "any source", List.of())))
                .build();
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

    @Ignore("TODO")
    @Test
    public void shouldThrowIfInvalidComponent() throws Exception {
        fail("WIP");
    }

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

    private ArchitectureUpdate changeAllTddsToBeUnderComponent(String newComponentId,
                                                               ArchitectureUpdate au) {
        final Map<Tdd.ComponentReference, Map<Tdd.Id, Tdd>> newTdds = new LinkedHashMap<>();
        for (var oldTdd : au.getTDDs().values()) {
            newTdds.put(new Tdd.ComponentReference(newComponentId), oldTdd);
        }
        return au.toBuilder().TDDs(newTdds).build();
    }
}

