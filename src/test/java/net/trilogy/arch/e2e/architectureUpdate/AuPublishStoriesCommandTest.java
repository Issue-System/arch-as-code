package net.trilogy.arch.e2e.architectureUpdate;

import net.trilogy.arch.Application;
import net.trilogy.arch.TestHelper;
import net.trilogy.arch.adapter.FilesFacade;
import net.trilogy.arch.adapter.Jira.JiraApi;
import net.trilogy.arch.adapter.Jira.JiraApiFactory;
import net.trilogy.arch.adapter.Jira.JiraQueryResult;
import net.trilogy.arch.adapter.Jira.JiraStory;
import net.trilogy.arch.adapter.Jira.JiraApi.JiraApiException;
import net.trilogy.arch.adapter.in.google.GoogleDocsAuthorizedApiFactory;
import net.trilogy.arch.domain.architectureUpdate.FunctionalRequirement;
import net.trilogy.arch.domain.architectureUpdate.Jira;
import net.trilogy.arch.domain.architectureUpdate.Tdd;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.List;

import static net.trilogy.arch.TestHelper.execute;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuPublishStoriesCommandTest {

    private File rootDir;
    private JiraApi mockedJiraApi;
    private Application app;

    final PrintStream originalOut = System.out;
    final PrintStream originalErr = System.err;
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    final ByteArrayOutputStream err = new ByteArrayOutputStream();

    @After
    public void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Before
    public void setUp() throws Exception {
        out.reset();
        err.reset();
        System.setOut(new PrintStream(out));
        System.setErr(new PrintStream(err));

        FilesFacade files = new FilesFacade();

        rootDir = new File(getClass().getResource(TestHelper.ROOT_PATH_TO_TEST_AU_PUBLISH).getPath());

        GoogleDocsAuthorizedApiFactory mockedGoogleApiFactory = mock(GoogleDocsAuthorizedApiFactory.class);

        final JiraApiFactory mockedJiraApiFactory = mock(JiraApiFactory.class);
        mockedJiraApi = mock(JiraApi.class);
        when(mockedJiraApiFactory.create(files, rootDir.toPath())).thenReturn(mockedJiraApi);

        app = new Application(mockedGoogleApiFactory, mockedJiraApiFactory, files);
    }

    @Test
    public void shouldQueryJiraForEpic() throws Exception {
        Jira epic = new Jira("[SAMPLE JIRA TICKET]", "[SAMPLE JIRA TICKET LINK]");

        execute(app, "au publish -u user -p password " + rootDir.getAbsolutePath() + "/architecture-updates/test.yml " + rootDir.getAbsolutePath());

        verify(mockedJiraApi).getStory(epic, "user", "password".toCharArray());
    }

    @Test
    public void shouldTellJiraToCreateStories() throws Exception {
        // GIVEN:
        Jira epic = Jira.blank();
        final JiraQueryResult epicInformation = new JiraQueryResult("PROJ_ID", "PROJ_KEY");
        when(mockedJiraApi.getStory(epic, "user", "password".toCharArray())).thenReturn(epicInformation);

        // WHEN:
        execute(app, "au publish -u user -p password " + rootDir.getAbsolutePath() + "/architecture-updates/test.yml " + rootDir.getAbsolutePath());

        // THEN:
        List<JiraStory> expected = List.of(getExpectedJiraStoriesToCreate());
        verify(mockedJiraApi).createStories(expected, epic.getTicket(), epicInformation.getProjectId(), epicInformation.getProjectKey(), "user", "password".toCharArray());
    }

    @Test
    public void shouldOutputResult() throws Exception {
        // GIVEN:
        Jira epic = Jira.blank();
        final JiraQueryResult epicInformation = new JiraQueryResult("PROJ_ID", "PROJ_KEY");
        when(mockedJiraApi.getStory(epic, "user", "password".toCharArray())).thenReturn(epicInformation);

        // WHEN:
        execute(app, "au publish -u user -p password " + rootDir.getAbsolutePath() + "/architecture-updates/test.yml " + rootDir.getAbsolutePath());

        // THEN:
        assertThat(
            out.toString(), 
            equalTo("Not re-creating stories:\n  - story that should not be created\nAttempting to create stories:\n  - story that should be created\n")
        );
    }

    @Ignore("TODO")
    @Test
    public void shouldUpdateAuWithNewJiraTickets() {
        // NOTE: even if partially failed
        fail("WIP");
    }

    @Ignore("TODO")
    @Test
    public void shouldDisplayPartialErrorsWhenCreatingStories() {
        fail("WIP");
    }

    @Test
    public void shouldDisplayNiceErrorIfCreatingStoriesCrashes() throws JiraApi.JiraApiException {
        when(mockedJiraApi.getStory(any(), any(), any())).thenReturn(new JiraQueryResult("ABC", "DEF"));
        when(mockedJiraApi.createStories(any(), any(), any(), any(), any(), any())).thenThrow(JiraApi.JiraApiException.builder().message("OOPS!").build());

        Integer statusCode = execute(app, "au publish -u user -p password " + rootDir.getAbsolutePath() + "/architecture-updates/test.yml " + rootDir.getAbsolutePath());

        assertThat(err.toString(), equalTo("ERROR: OOPS!\n"));
        assertThat(out.toString(), equalTo("Not re-creating stories:\n  - story that should not be created\nAttempting to create stories:\n  - story that should be created\n"));
        assertThat(statusCode, not(equalTo(0)));
    }

    @Test
    public void shouldDisplayGetStoryErrorsFromJira() throws JiraApi.JiraApiException {
        Jira epic = new Jira("[SAMPLE JIRA TICKET]", "[SAMPLE JIRA TICKET LINK]");

        when(mockedJiraApi.getStory(epic, "user", "password".toCharArray())).thenThrow(JiraApi.JiraApiException.builder().message("OOPS!").build());

        Integer statusCode = execute(app, "au publish -u user -p password " + rootDir.getAbsolutePath() + "/architecture-updates/test.yml " + rootDir.getAbsolutePath());

        assertThat(err.toString(), equalTo("ERROR: OOPS!\n"));
        assertThat(out.toString(), equalTo("Not re-creating stories:\n  - story that should not be created\n"));
        assertThat(statusCode, not(equalTo(0)));
    }

    private JiraStory getExpectedJiraStoriesToCreate() {
        return new JiraStory(
                "story that should be created",
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
