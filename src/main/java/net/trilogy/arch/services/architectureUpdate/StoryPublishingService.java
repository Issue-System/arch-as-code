package net.trilogy.arch.services.architectureUpdate;

import net.trilogy.arch.adapter.Jira.JiraApi;
import net.trilogy.arch.adapter.Jira.JiraCreateStoryStatus;
import net.trilogy.arch.adapter.Jira.JiraStory;
import net.trilogy.arch.domain.architectureUpdate.ArchitectureUpdate;
import net.trilogy.arch.domain.architectureUpdate.FeatureStory;
import net.trilogy.arch.domain.architectureUpdate.Jira;

import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

public class StoryPublishingService {

    private final JiraApi api;
    private final PrintWriter out;
    private final PrintWriter err;

    public StoryPublishingService(final PrintWriter out, final PrintWriter err, final JiraApi jiraApi) {
        this.out = out;
        this.err = err;
        this.api = jiraApi;
    }

    public ArchitectureUpdate createStories(
            final ArchitectureUpdate au,
            String username,
            char[] password
    ) throws JiraApi.JiraApiException {
        printStoriesNotToBeSent(au);

        out.println("\nAttempting to create stories...\n");

        final var epicJiraTicket = au.getCapabilityContainer().getEpic().getJira();
        final var informationAboutTheEpic = this.api.getStory(epicJiraTicket, username, password);
        final var stories = getFeatureStoriesToCreate(au);

        var createStoriesResults = this.api.createStories(
                stories.stream()
                        .map(fs -> new JiraStory(au, fs))
                        .collect(Collectors.toList()),
                epicJiraTicket.getTicket(),
                informationAboutTheEpic.getProjectId(),
                informationAboutTheEpic.getProjectKey(),
                username,
                password
        );

        printStoriesThatSucceeded(stories, createStoriesResults);
        printStoriesThatFailed(stories, createStoriesResults);

        return updateJiraTicketsInAu(au, stories, createStoriesResults);
    }

    private void printStoriesThatSucceeded(List<FeatureStory> stories, List<JiraCreateStoryStatus> createStoriesResults) {
        StringBuilder successfulStories = new StringBuilder();

        for(int i = 0; i < createStoriesResults.size(); ++i) {
            if(!createStoriesResults.get(i).isSucceeded()) continue;
            successfulStories.append("\n  - ").append(stories.get(i).getTitle());
        }

        String heading = "Successfully created:";

        if(!successfulStories.toString().isBlank()){
            out.println(heading + successfulStories);
        }
    }

    private void printStoriesThatFailed(List<FeatureStory> stories, List<JiraCreateStoryStatus> createStoriesResults) {
        StringBuilder errors = new StringBuilder();
        for(int i = 0; i < createStoriesResults.size(); ++i) {
            if(createStoriesResults.get(i).isSucceeded()) continue;
            errors.append("Story: \"").append(stories.get(i).getTitle()).append("\":\n  - ").append(createStoriesResults.get(i).getError());
        }
        String heading = "Error! Some stories failed to publish. Please retry. Errors reported by Jira:";
        if(!errors.toString().isBlank()){
            err.println("\n" + heading + "\n\n" + errors);
        }
    }

    private static ArchitectureUpdate updateJiraTicketsInAu(ArchitectureUpdate au, List<FeatureStory> stories, List<JiraCreateStoryStatus> createStoriesResults) {
        ArchitectureUpdate resultingAu = au;
        for (int i = 0; i < createStoriesResults.size(); ++i) {
            if (createStoriesResults.get(i).isSucceeded()) {
                resultingAu = resultingAu.addJiraToFeatureStory(
                        stories.get(i),
                        new Jira(createStoriesResults.get(i).getIssueKey(), createStoriesResults.get(i).getIssueLink())
                );
            }
        }
        return resultingAu;
    }

    private void printStoriesNotToBeSent(final ArchitectureUpdate au) {
        String stories = au.getCapabilityContainer()
                .getFeatureStories()
                .stream()
                .filter(story -> !shouldCreateStory(story))
                .map(story -> "  - " + story.getTitle())
                .collect(Collectors.joining("\n"));
        if (!stories.isBlank()) {
            out.println("Not re-creating stories:\n" + stories);
        }
    }

    private static List<FeatureStory> getFeatureStoriesToCreate(final ArchitectureUpdate au) {
        return au.getCapabilityContainer()
                .getFeatureStories()
                .stream()
                .filter(StoryPublishingService::shouldCreateStory)
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

