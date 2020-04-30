package net.trilogy.arch.e2e.architectureUpdate;

import net.trilogy.arch.Application;
import net.trilogy.arch.adapter.FilesFacade;
import net.trilogy.arch.adapter.Jira.JiraApi;
import net.trilogy.arch.adapter.Jira.JiraApiFactory;
import net.trilogy.arch.adapter.in.google.GoogleDocsAuthorizedApiFactory;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static net.trilogy.arch.TestHelper.execute;
import static org.mockito.Mockito.*;

public class AuPublishStoriesCommandTest {

    @Test
    public void shouldCreateJiraStories() throws IOException, InterruptedException {
        final Path rootDir = Files.createTempDirectory("arch-as-code_architecture-update_command_tests");
        final FilesFacade filesFacade = new FilesFacade();
        final GoogleDocsAuthorizedApiFactory mockedGoogleApiFactory = mock(GoogleDocsAuthorizedApiFactory.class);
        final JiraApiFactory mockedJiraApiFactory = mock(JiraApiFactory.class);
        final JiraApi mockedJiraApi = mock(JiraApi.class);
        when(mockedJiraApiFactory.create(filesFacade)).thenReturn(mockedJiraApi);

        final Application app = new Application(mockedGoogleApiFactory, mockedJiraApiFactory, filesFacade);
        execute(app, "au publish -u user -p password " + rootDir.toAbsolutePath().toString());

        verify(mockedJiraApi).createStory();
    }
}
