package net.trilogy.arch.adapter.Jira;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

import java.io.IOException;
import java.net.http.HttpClient;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class JiraApiTest {

    private HttpClient mockHttpClient;
    private JiraApi jiraApi;

    @Before
    public void setUp() {
        mockHttpClient = mock(HttpClient.class);
        jiraApi = new JiraApi(mockHttpClient);
    }

    @Test
    public void shouldCreateStory() throws IOException, InterruptedException {
        jiraApi.createStory();

        verify(mockHttpClient).send(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

}
