package net.trilogy.arch.adapter.Jira;

import net.trilogy.arch.adapter.FilesFacade;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.http.HttpClient;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import static net.trilogy.arch.adapter.Jira.JiraApiFactory.JIRA_API_SETTINGS_FILE_PATH;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JiraApiFactoryTest {
    @Rule
    public final ErrorCollector collector = new ErrorCollector();
    private FilesFacade mockedFiles;
    private final String expectedBaseUri = "BASE-URI/";
    private final String expectedGetStoryEndpoint = "GET-STORY-ENDPOINT/";
    private final String expectedBulkCreateEndpoint = "BULK-CREATE-ENDPOINT/";

    @Before
    public void setUp() throws Exception {
        String json = "" +
                "{\n" +
                "    \"base_uri\": \"" + expectedBaseUri + "\",\n" +
                "    \"get_story_endpoint\": \"" + expectedGetStoryEndpoint + "\",\n" +
                "    \"bulk_create_endpoint\": \"" + expectedBulkCreateEndpoint + "\"\n" +
                "}";
        mockedFiles = mock(FilesFacade.class);
        when(
                mockedFiles.readString(Path.of(JIRA_API_SETTINGS_FILE_PATH))
        ).thenReturn(json);
    }

    @Test
    public void shouldUseTheRightConstants() {
        assertThat(JIRA_API_SETTINGS_FILE_PATH, equalTo(".arch-as-code/jira/settings.json"));
    }

    @Test
    public void shouldCreateJiraApiWithCorrectClient() throws IOException {
        final JiraApiFactory factory = new JiraApiFactory();
        HttpClient client = factory.createClient();
        JiraApi jiraApi = factory.create(mockedFiles);

        collector.checkThat(jiraApi.getHttpClient(), is(client));
        collector.checkThat(jiraApi.getBaseUri(), equalTo(expectedBaseUri));
        collector.checkThat(jiraApi.getGetStoryEndpoint(), equalTo(expectedGetStoryEndpoint));
        collector.checkThat(jiraApi.getBulkCreateEndpoint(), equalTo(expectedBulkCreateEndpoint));
    }


    @Test
    public void shouldCreateCorrectClient() throws NoSuchAlgorithmException, IOException {

        final JiraApiFactory factory = new JiraApiFactory();

        HttpClient client = factory.createClient();

        assertThat(client.connectTimeout(), equalTo(Optional.empty()));
        assertThat(client.authenticator(), equalTo(Optional.empty()));
        assertThat(client.cookieHandler(), equalTo(Optional.empty()));
        assertThat(client.executor(), equalTo(Optional.empty()));
        assertThat(client.proxy(), equalTo(Optional.empty()));
        assertThat(client.followRedirects(), equalTo(HttpClient.Redirect.NORMAL));
        assertThat(client.sslContext(), equalTo(SSLContext.getDefault()));
        assertThat(client.version(), equalTo(HttpClient.Version.HTTP_2));
    }
}
