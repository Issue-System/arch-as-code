package net.trilogy.arch.adapter.Jira;

import org.junit.Test;

import javax.net.ssl.SSLContext;
import java.net.http.HttpClient;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class JiraApiFactoryTest {

    @Test
    public void shouldCreateJiraApiWithCorrectClient() {
        final JiraApiFactory factory = new JiraApiFactory();

        HttpClient client = factory.createClient();
        JiraApi jiraApi = factory.create();

        assertThat(jiraApi.getHttpClient(), is(client));
    }

    @Test
    public void shouldCreateCorrectClient() throws NoSuchAlgorithmException {
        final JiraApiFactory factory = new JiraApiFactory();

        HttpClient client = factory.createClient();

        assertThat(client.connectTimeout(), equalTo(Optional.empty()));
        assertThat(client.authenticator(), equalTo(Optional.empty()));
        assertThat(client.cookieHandler(), equalTo(Optional.empty()));
        assertThat(client.executor(), equalTo(Optional.empty()));
        assertThat(client.proxy(), equalTo(Optional.empty()));
        assertThat(client.followRedirects(), equalTo(HttpClient.Redirect.NEVER));
        assertThat(client.sslContext(), equalTo(SSLContext.getDefault()));
        assertThat(client.version(), equalTo(HttpClient.Version.HTTP_2));
    }
}
