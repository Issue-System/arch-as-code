package net.trilogy.arch.e2e.architectureUpdate;

import net.trilogy.arch.Application;
import net.trilogy.arch.TestHelper;
import net.trilogy.arch.adapter.FilesFacade;
import net.trilogy.arch.adapter.Jira.JiraApi;
import net.trilogy.arch.adapter.Jira.JiraApiFactory;
import net.trilogy.arch.adapter.Jira.JiraQueryResult;
import net.trilogy.arch.adapter.Jira.JiraStory;
import net.trilogy.arch.adapter.in.google.GoogleDocsAuthorizedApiFactory;
import net.trilogy.arch.domain.architectureUpdate.FunctionalRequirement;
import net.trilogy.arch.domain.architectureUpdate.Jira;
import net.trilogy.arch.domain.architectureUpdate.Tdd;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static net.trilogy.arch.TestHelper.execute;
import static org.mockito.Mockito.*;

public class AuPublishStoriesCommandTest {

    private File rootDir;
    private JiraApi mockedJiraApi;
    private Application app;

    @Before
    public void setUp() throws Exception {
        FilesFacade files = new FilesFacade();

        rootDir = new File(getClass().getResource(TestHelper.ROOT_PATH_TO_TEST_AU_PUBLISH).getPath());

        GoogleDocsAuthorizedApiFactory mockedGoogleApiFactory = mock(GoogleDocsAuthorizedApiFactory.class);

        final JiraApiFactory mockedJiraApiFactory = mock(JiraApiFactory.class);
        mockedJiraApi = mock(JiraApi.class);
        when(mockedJiraApiFactory.create(files, rootDir.toPath())).thenReturn(mockedJiraApi);

        app = new Application(mockedGoogleApiFactory, mockedJiraApiFactory, files);
    }

    @Test
    public void shouldQueryJiraForEpic() throws IOException, InterruptedException {
        Jira epic = new Jira("[SAMPLE JIRA TICKET]", "[SAMPLE JIRA TICKET LINK]");

        execute(app, "au publish -u user -p password " + rootDir.getAbsolutePath() + "/architecture-updates/test.yml " + rootDir.getAbsolutePath());

        verify(mockedJiraApi).getStory(epic, "user", "password".toCharArray());
    }

    @Test
    public void shouldTellJiraToCreateStories() throws IOException, InterruptedException {
        Jira epic = new Jira("[SAMPLE JIRA TICKET]", "[SAMPLE JIRA TICKET LINK]");
        List<JiraStory> jiraStories = List.of(createSampleJiraStory());

        final JiraQueryResult epicInformation = new JiraQueryResult();
        when(mockedJiraApi.getStory(epic, "user", "password".toCharArray())).thenReturn(epicInformation);

        execute(app, "au publish -u user -p password " + rootDir.getAbsolutePath() + "/architecture-updates/test.yml " + rootDir.getAbsolutePath());

        verify(mockedJiraApi).createStories(jiraStories, epicInformation);
    }

    private JiraStory createSampleJiraStory() {
        return new JiraStory(
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
    }
}
