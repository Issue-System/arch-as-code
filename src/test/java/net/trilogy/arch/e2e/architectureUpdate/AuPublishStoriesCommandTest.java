package net.trilogy.arch.e2e.architectureUpdate;

import net.trilogy.arch.Application;
import net.trilogy.arch.TestHelper;
import net.trilogy.arch.adapter.architectureUpdateYaml.ArchitectureUpdateObjectMapper;
import net.trilogy.arch.adapter.architectureYaml.ArchitectureDataStructureObjectMapper;
import net.trilogy.arch.adapter.git.GitInterface;
import net.trilogy.arch.adapter.google.GoogleDocsAuthorizedApiFactory;
import net.trilogy.arch.adapter.jira.*;
import net.trilogy.arch.domain.architectureUpdate.ArchitectureUpdate;
import net.trilogy.arch.domain.architectureUpdate.FunctionalRequirement;
import net.trilogy.arch.domain.architectureUpdate.Jira;
import net.trilogy.arch.domain.architectureUpdate.Tdd;
import net.trilogy.arch.facade.FilesFacade;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static net.trilogy.arch.TestHelper.execute;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

public class AuPublishStoriesCommandTest {
    @Rule
    public final ErrorCollector collector = new ErrorCollector();

    private File rootDir;
    private JiraApi mockedJiraApi;
    private Application app;
    private FilesFacade spiedFilesFacade;
    private GitInterface mockedGitInterface;

    final PrintStream originalOut = System.out;
    final PrintStream originalErr = System.err;
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    final ByteArrayOutputStream err = new ByteArrayOutputStream();

    @After
    public void tearDown() throws Exception {
        System.setOut(originalOut);
        System.setErr(originalErr);

        Files.deleteIfExists(rootDir.toPath().resolve("architecture-updates/test-clone.yml"));
    }

    @Before
    public void setUp() throws Exception {
        out.reset();
        err.reset();
        System.setOut(new PrintStream(out));
        System.setErr(new PrintStream(err));

        spiedFilesFacade = spy(new FilesFacade());

        rootDir = new File(getClass().getResource(TestHelper.ROOT_PATH_TO_TEST_AU_PUBLISH).getPath());

        GoogleDocsAuthorizedApiFactory mockedGoogleApiFactory = mock(GoogleDocsAuthorizedApiFactory.class);

        final JiraApiFactory mockedJiraApiFactory = mock(JiraApiFactory.class);
        mockedJiraApi = mock(JiraApi.class);
        when(mockedJiraApiFactory.create(spiedFilesFacade, rootDir.toPath())).thenReturn(mockedJiraApi);

        mockedGitInterface = mock(GitInterface.class);

        app = new Application(mockedGoogleApiFactory, mockedJiraApiFactory, spiedFilesFacade, mockedGitInterface);

        Files.copy(rootDir.toPath().resolve("architecture-updates/test.yml"), rootDir.toPath().resolve("architecture-updates/test-clone.yml"));
    }

    @Test
    public void shouldFailGracefullyIfFailToLoadConfig() throws Exception {
        Path auPath = rootDir.toPath().resolve("architecture-updates/test-clone.yml");
        mockGitInterface();

        var newApp = new Application(new GoogleDocsAuthorizedApiFactory(), new JiraApiFactory(), new FilesFacade(), mockedGitInterface);

        int status = execute(newApp, "au publish -b master -u user -p password " + auPath.toAbsolutePath().toString() + " " + rootDir.getAbsolutePath());

        collector.checkThat(status, not(equalTo(0)));
        collector.checkThat(out.toString(), equalTo(""));
        collector.checkThat(err.toString(), containsString("Unable to load configuration.\nError thrown: java.nio.file.NoSuchFileException"));
    }

    @Test
    public void shouldFailGracefullyIfFailToLoadAu() throws Exception {
        Path auPath = rootDir.toPath().resolve("architecture-updates/test-clone.yml");
        doThrow(new RuntimeException("ERROR", new RuntimeException("DETAILS"))).when(spiedFilesFacade).readString(eq(auPath));

        int status = execute(app, "au publish -b master -u user -p password " + auPath.toAbsolutePath().toString() + " " + rootDir.getAbsolutePath());

        collector.checkThat(status, not(equalTo(0)));
        collector.checkThat(out.toString(), equalTo(""));
        collector.checkThat(err.toString(), equalTo("Unable to load architecture update.\nError thrown: java.lang.RuntimeException: ERROR\nCause: java.lang.RuntimeException: DETAILS\n"));
    }

    @Test
    public void shouldFailGracefullyIfFailToLoadArchitecture() throws Exception {
        doThrow(new RuntimeException("ERROR", new RuntimeException("DETAILS"))).when(spiedFilesFacade).readString(eq(rootDir.toPath().resolve("product-architecture.yml")));

        int status = execute(app, "au publish -b master -u user -p password " + rootDir.getAbsolutePath() + "/architecture-updates/test-clone.yml " + rootDir.getAbsolutePath());

        collector.checkThat(status, not(equalTo(0)));
        collector.checkThat(out.toString(), equalTo(""));
        collector.checkThat(err.toString(), equalTo("Unable to load architecture.\nError thrown: java.lang.RuntimeException: ERROR\nCause: java.lang.RuntimeException: DETAILS\n"));
    }

    @Test
    public void shouldFailGracefullyIfUnableToCreateJiraStoryDTO() throws Exception {
        Jira epic = Jira.blank();
        final JiraQueryResult epicInformation = new JiraQueryResult("PROJ_ID", "PROJ_KEY");
        when(mockedJiraApi.getStory(epic, "user", "password".toCharArray())).thenReturn(epicInformation);

        int status = execute(app, "au publish -b master -u user -p password " + rootDir.getAbsolutePath() + "/architecture-updates/invalid-story.yml " + rootDir.getAbsolutePath());

        collector.checkThat(status, not(equalTo(0)));
        collector.checkThat(
                out.toString(),
                equalTo(
                        "Not re-creating stories:\n  - story that should not be created\n\n" +
                                "Checking epic...\n\n" +
                                "Attempting to create stories...\n\n"
                )
        );
        collector.checkThat(err.toString(), equalTo("ERROR: Some stories are invalid. Please run 'au validate' command.\n"));
    }

    @Test
    public void shouldQueryJiraForEpic() throws Exception {
        Jira epic = new Jira("[SAMPLE JIRA TICKET]", "[SAMPLE JIRA TICKET LINK]");

        execute(app, "au publish -b master -u user -p password " + rootDir.getAbsolutePath() + "/architecture-updates/test-clone.yml " + rootDir.getAbsolutePath());

        verify(mockedJiraApi).getStory(epic, "user", "password".toCharArray());
    }

    @Test
    public void shouldTellJiraToCreateStories() throws Exception {
        // GIVEN:
        Jira epic = Jira.blank();
        final JiraQueryResult epicInformation = new JiraQueryResult("PROJ_ID", "PROJ_KEY");
        when(mockedJiraApi.getStory(epic, "user", "password".toCharArray())).thenReturn(epicInformation);
        mockGitInterface();

        // WHEN:
        execute(app, "au publish -b master -u user -p password " + rootDir.getAbsolutePath() + "/architecture-updates/test-clone.yml " + rootDir.getAbsolutePath());

        // THEN:
        List<JiraStory> expected = getExpectedJiraStoriesToCreate();
        verify(mockedJiraApi).createStories(expected, epic.getTicket(), epicInformation.getProjectId(), epicInformation.getProjectKey(), "user", "password".toCharArray());
    }

    private void mockGitInterface() throws IOException, GitAPIException, GitInterface.BranchNotFoundException {
        when(mockedGitInterface.load("master", rootDir.toPath().resolve("product-architecture.yml")))
                .thenReturn(
                        new ArchitectureDataStructureObjectMapper()
                                .readValue(
                                        Files.readString(
                                                rootDir.toPath().resolve("product-architecture.yml"))
                                                .replaceAll("34", "DELETED-COMPONENT-ID")
                                )
                );
    }

    @Test
    public void shouldOutputResult() throws Exception {
        // GIVEN:
        Jira epic = Jira.blank();
        final JiraQueryResult epicInformation = new JiraQueryResult("PROJ_ID", "PROJ_KEY");
        when(mockedJiraApi.getStory(epic, "user", "password".toCharArray())).thenReturn(epicInformation);
        when(
                mockedJiraApi.createStories(any(), any(), any(), any(), any(), any())
        ).thenReturn(List.of(
                JiraCreateStoryStatus.succeeded("ABC-123", "link-to-ABC-123"),
                JiraCreateStoryStatus.succeeded("ABC-223", "link-to-ABC-223")
        ));
        mockGitInterface();

        // WHEN:
        execute(app, "au publish -b master -u user -p password " + rootDir.getAbsolutePath() + "/architecture-updates/test-clone.yml " + rootDir.getAbsolutePath());

        // THEN:
        collector.checkThat(
                out.toString(),
                equalTo(
                        "Not re-creating stories:\n  - story that should not be created\n\n" +
                                "Checking epic...\n\n" +
                                "Attempting to create stories...\n\n" +
                                "Successfully created:\n  - story that should be created\n  - story that failed to be created\n"
                )
        );
        collector.checkThat(err.toString(), equalTo(""));
    }

    @Test
    public void shouldUpdateAuWithNewJiraTickets() throws Exception {
        // GIVEN:
        Jira epic = Jira.blank();
        final JiraQueryResult epicInformation = new JiraQueryResult("PROJ_ID", "PROJ_KEY");
        when(mockedJiraApi.getStory(epic, "user", "password".toCharArray())).thenReturn(epicInformation);
        when(
                mockedJiraApi.createStories(any(), any(), any(), any(), any(), any())
        ).thenReturn(List.of(
                JiraCreateStoryStatus.succeeded("ABC-123", "link-to-ABC-123"),
                JiraCreateStoryStatus.failed("error-message")
        ));
        mockGitInterface();

        // WHEN:
        execute(app, "au publish -b master -u user -p password " + rootDir.getAbsolutePath() + "/architecture-updates/test-clone.yml " + rootDir.getAbsolutePath());
        String actualAuAsstring = Files.readString(rootDir.toPath().resolve("architecture-updates/test-clone.yml"));
        ArchitectureUpdate actualAu = new ArchitectureUpdateObjectMapper().readValue(actualAuAsstring);

        // THEN:
        String originalAuAsString = Files.readString(rootDir.toPath().resolve("architecture-updates/test.yml"));
        ArchitectureUpdate originalAu = new ArchitectureUpdateObjectMapper().readValue(originalAuAsString);
        ArchitectureUpdate expectedAu = originalAu.addJiraToFeatureStory(
                originalAu.getCapabilityContainer().getFeatureStories().get(0), new Jira("ABC-123", "link-to-ABC-123")
        );

        collector.checkThat(actualAu, equalTo(expectedAu));
    }

    @Test
    public void shouldDisplayPartialErrorsWhenCreatingStories() throws Exception {
        // GIVEN:
        Jira epic = Jira.blank();
        final JiraQueryResult epicInformation = new JiraQueryResult("PROJ_ID", "PROJ_KEY");
        when(mockedJiraApi.getStory(epic, "user", "password".toCharArray())).thenReturn(epicInformation);
        when(
                mockedJiraApi.createStories(any(), any(), any(), any(), any(), any())
        ).thenReturn(List.of(
                JiraCreateStoryStatus.succeeded("ABC-123", "link-to-ABC-123"),
                JiraCreateStoryStatus.failed("error-message")
        ));
        mockGitInterface();

        // WHEN:
        int statusCode = execute(app, "au publish -b master -u user -p password " + rootDir.getAbsolutePath() + "/architecture-updates/test-clone.yml " + rootDir.getAbsolutePath());

        // THEN:
        assertThat(
                err.toString(),
                equalTo(
                        "\nError! Some stories failed to publish. Please retry. Errors reported by Jira:\n\nStory: \"story that failed to be created\":\n  - error-message\n"
                )
        );
        assertThat(
                out.toString(),
                equalTo(
                        "Not re-creating stories:\n  - story that should not be created\n\n" +
                                "Checking epic...\n\n" +
                                "Attempting to create stories...\n\nSuccessfully created:\n  - story that should be created\n"
                )
        );
        assertThat(statusCode, equalTo(0));
    }

    @Test
    public void shouldDisplayNiceErrorIfCreatingStoriesCrashes() throws Exception {
        when(mockedJiraApi.getStory(any(), any(), any())).thenReturn(new JiraQueryResult("ABC", "DEF"));
        when(mockedJiraApi.createStories(any(), any(), any(), any(), any(), any())).thenThrow(JiraApi.JiraApiException.builder().message("OOPS!").cause(new RuntimeException("Details")).build());
        mockGitInterface();

        Integer statusCode = execute(app, "au publish -b master -u user -p password " + rootDir.getAbsolutePath() + "/architecture-updates/test-clone.yml " + rootDir.getAbsolutePath());

        assertThat(err.toString(), equalTo("ERROR: OOPS!\n\nDetails\n\n"));
        assertThat(
                out.toString(),
                equalTo(
                        "Not re-creating stories:\n  - story that should not be created\n\n" +
                                "Checking epic...\n\n" +
                                "Attempting to create stories...\n\n"
                )
        );
        assertThat(statusCode, not(equalTo(0)));
    }

    @Test
    public void shouldHandleNoStoriesToCreate() {
        Integer statusCode = execute(app, "au publish -b master -u user -p password " + rootDir.getAbsolutePath() + "/architecture-updates/no-stories-to-create.yml " + rootDir.getAbsolutePath());

        verifyNoMoreInteractions(mockedJiraApi);
        collector.checkThat(err.toString(), equalTo("ERROR: No stories to create.\n"));
        collector.checkThat(
                out.toString(),
                equalTo(
                        "Not re-creating stories:\n  - story that should not be created\n\n"
                )
        );
        collector.checkThat(statusCode, not(equalTo(0)));
    }

    @Test
    public void shouldDisplayGetStoryErrorsFromJira() throws Exception {
        Jira epic = new Jira("[SAMPLE JIRA TICKET]", "[SAMPLE JIRA TICKET LINK]");

        when(mockedJiraApi.getStory(epic, "user", "password".toCharArray())).thenThrow(JiraApi.JiraApiException.builder().message("OOPS!").build());

        Integer statusCode = execute(app, "au publish -b master -u user -p password " + rootDir.getAbsolutePath() + "/architecture-updates/test-clone.yml " + rootDir.getAbsolutePath());

        assertThat(err.toString(), equalTo("ERROR: OOPS!\n\n"));
        assertThat(out.toString(), equalTo("Not re-creating stories:\n  - story that should not be created\n\nChecking epic...\n\n"));
        assertThat(statusCode, not(equalTo(0)));
    }

    private List<JiraStory> getExpectedJiraStoriesToCreate() {
        return List.of(
                new JiraStory(
                        "story that should be created",
                        List.of(
                                new JiraStory.JiraTdd(
                                        new Tdd.Id("[SAMPLE-TDD-ID]"),
                                        new Tdd("[SAMPLE TDD TEXT]"),
                                        "c4://Internet Banking System/API Application/Reset Password Controller"
                                ),
                                new JiraStory.JiraTdd(
                                        new Tdd.Id("[SAMPLE-TDD-ID-2]"),
                                        new Tdd("[SAMPLE TDD TEXT]"),
                                        "c4://Internet Banking System/API Application/E-mail Component"
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
                ),
                new JiraStory(
                        "story that failed to be created",
                        List.of(
                                new JiraStory.JiraTdd(
                                        new Tdd.Id("[SAMPLE-TDD-ID]"),
                                        new Tdd("[SAMPLE TDD TEXT]"),
                                        "c4://Internet Banking System/API Application/Reset Password Controller"
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
                )
        );
    }
}

