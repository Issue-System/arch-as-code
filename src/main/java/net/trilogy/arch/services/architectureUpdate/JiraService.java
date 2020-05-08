package net.trilogy.arch.services.architectureUpdate;

import net.trilogy.arch.adapter.Jira.JiraApi;
import net.trilogy.arch.adapter.Jira.JiraCreateStoryStatus;
import net.trilogy.arch.adapter.Jira.JiraStory;
import net.trilogy.arch.domain.architectureUpdate.ArchitectureUpdate;
import net.trilogy.arch.domain.architectureUpdate.FeatureStory;

import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

public class JiraService {

    private final JiraApi api;
    private final PrintWriter out;

    public JiraService(final PrintWriter out, final JiraApi jiraApi) {
        this.out = out;
        this.api = jiraApi;
    }

    public void createStories(final ArchitectureUpdate au,
            String username,
            char[] password) throws JiraApi.JiraApiException {

        printStoriesNotToBeSent(au);

        final var epicJiraTicket = au.getCapabilityContainer().getEpic().getJira();
        final var informationAboutTheEpic = this.api.getStory(epicJiraTicket, username, password);

        var storiesToCreate = getJiraStoriesToCreate(au);

        printStoriesThatWereSent(storiesToCreate);

        var createStoriesResults = this.api.createStories(
                storiesToCreate,
                epicJiraTicket.getTicket(),
                informationAboutTheEpic.getProjectId(),
                informationAboutTheEpic.getProjectKey(),
                username,
                password
        );

        // au.toBuilder()
        //     .capabilityContainer(
        //             au.getCapabilityContainer().toBuilder().featureStories(newFeatureStories)
        //     )
        //     .build();

        // for(int i = 0; i < 
    }

    private void printStoriesNotToBeSent(final ArchitectureUpdate au) {
        String stories = au.getCapabilityContainer()
            .getFeatureStories()
            .stream()
            .filter(story -> !shouldCreateStory(story))
            .map(story -> "  - " + story.getTitle())
            .collect(Collectors.joining("\n"));
        if(!stories.isBlank()) {
            out.println("Not re-creating stories:\n" + stories);
        }
    }

    private void printStoriesThatWereSent(final List<JiraStory> stories) {
        String sent = stories.stream().map(story -> "  - " + story.getTitle()).collect(Collectors.joining("\n"));
        if(!sent.isBlank()) {
            out.println("Attempting to create stories:\n" + sent);
        }
    }

    private List<JiraStory> getJiraStoriesToCreate(final ArchitectureUpdate au) {
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
