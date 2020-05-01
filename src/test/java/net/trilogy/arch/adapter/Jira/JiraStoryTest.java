package net.trilogy.arch.adapter.Jira;

import net.trilogy.arch.domain.architectureUpdate.ArchitectureUpdate;
import net.trilogy.arch.domain.architectureUpdate.FunctionalRequirement;
import net.trilogy.arch.domain.architectureUpdate.Tdd;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class JiraStoryTest {

    @Test
    public void ShouldConstructJiraStory() {
        var au = ArchitectureUpdate.blank();
        var featureStory = au.getCapabilityContainer().getFeatureStories().get(0);

        final JiraStory jiraStory = new JiraStory(au, featureStory);
        final JiraStory expected = new JiraStory(
                "[SAMPLE FEATURE STORY TITLE]",
                List.of(
                        new JiraStory.JiraTdd(
                                new Tdd.Id("[SAMPLE-TDD-ID]"),
                                new Tdd("[SAMPLE TDD TEXT]"),
                                new Tdd.ComponentReference("Component-[SAMPLE-COMPONENT-ID]")
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

        assertThat(jiraStory, equalTo(expected));
    }

}
