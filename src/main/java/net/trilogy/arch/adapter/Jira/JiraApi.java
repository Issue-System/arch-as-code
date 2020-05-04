package net.trilogy.arch.adapter.Jira;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.List;

import com.google.common.annotations.VisibleForTesting;

import net.trilogy.arch.domain.architectureUpdate.Jira;

public class JiraApi {
    private final HttpClient client;
    private final String baseUri;
    private final String bulkCreateEndpoint;

    public JiraApi(HttpClient client, String baseUri, String bulkCreateEndpoint) {
        this.client = client;
        this.baseUri = baseUri;
        this.bulkCreateEndpoint = bulkCreateEndpoint;
    }

    public HttpResponse<String> createStories(List<JiraStory> jiraStories, JiraQueryResult epicInformation) throws IOException, InterruptedException {
        throw new UnsupportedOperationException();
    }

    public JiraQueryResult getStory(Jira jira) throws IOException, InterruptedException {
        this.client.send(null, HttpResponse.BodyHandlers.ofString());
        return new JiraQueryResult();
    }

    @VisibleForTesting
    HttpClient getHttpClient() {
        return client;
    }

    @VisibleForTesting
    String getBaseUri() {
        return baseUri;
    }

    @VisibleForTesting
    String getBulkCreateEndpoint() {
        return bulkCreateEndpoint;
    }
}
