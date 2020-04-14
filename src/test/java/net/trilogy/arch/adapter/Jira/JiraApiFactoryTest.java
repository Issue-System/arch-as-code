package net.trilogy.arch.adapter.Jira;

import org.junit.Ignore;
import org.junit.Test;

import java.net.http.HttpClient;

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
    @Ignore
    public void shouldCreateCorrectClient() {
        final JiraApiFactory factory = new JiraApiFactory();

        HttpClient client = factory.createClient();

        assertThat(client.authenticator(), equalTo(null));
    }
}
