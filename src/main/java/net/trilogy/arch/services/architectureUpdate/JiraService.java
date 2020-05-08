package net.trilogy.arch.services.architectureUpdate;

import net.trilogy.arch.adapter.Jira.JiraApi;
import net.trilogy.arch.adapter.Jira.JiraCreateStoryStatus;
import net.trilogy.arch.adapter.Jira.JiraStory;
import net.trilogy.arch.domain.architectureUpdate.ArchitectureUpdate;
import net.trilogy.arch.domain.architectureUpdate.FeatureStory;

import java.util.List;
import java.util.stream.Collectors;

public class JiraService {

    private final JiraApi api;

    public JiraService(final JiraApi jiraApi) {
        this.api = jiraApi;
    }

    public void createStories(final ArchitectureUpdate au,
                              String username,
                              char[] password) throws JiraApi.JiraApiException {
        final var epicJiraTicket = au.getCapabilityContainer().getEpic().getJira();
        final var informationAboutTheEpic = this.api.getStory(epicJiraTicket, username, password);

        var storiesToCreate = getFeatureStories(au);
        var createStoriesResults = this.api.createStories(storiesToCreate,
                epicJiraTicket.getTicket(),
                informationAboutTheEpic.getProjectId(),
                informationAboutTheEpic.getProjectKey(),
                username,
                password);
    }

    private List<JiraStory> getFeatureStories(final ArchitectureUpdate au) {
        return au.getCapabilityContainer()
                .getFeatureStories()
                .stream()
                .filter(JiraService::shouldCreateStory)
                .map(fs -> new JiraStory(au, fs))
                .collect(Collectors.toList());
    }

    private static boolean shouldCreateStory(FeatureStory story) {
        return ! isStoryAlreadyCreated(story);
    }

    private static boolean isStoryAlreadyCreated(FeatureStory story) {
        return !(
            story.getJira().getTicket().isBlank() && 
            story.getJira().getLink().isBlank()
        );
    }
}
