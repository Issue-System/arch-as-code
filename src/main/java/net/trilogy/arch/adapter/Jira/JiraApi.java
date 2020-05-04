package net.trilogy.arch.adapter.Jira;

import com.google.common.annotations.VisibleForTesting;
import net.trilogy.arch.domain.architectureUpdate.Jira;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.List;

public class JiraApi {
    private final HttpClient client;
    private final String baseUri;
    private final String getStoryEndpoint;
    private final String bulkCreateEndpoint;

    public JiraApi(HttpClient client, String baseUri, String getStoryEndpoint, String bulkCreateEndpoint) {
        this.client = client;
        this.baseUri = baseUri.replaceAll("/$", "") + "/";
        this.bulkCreateEndpoint = bulkCreateEndpoint.replaceAll("(^/|/$)", "") + "/";
        this.getStoryEndpoint = getStoryEndpoint.replaceAll("(^/|/$)", "") + "/";
    }

    public HttpResponse<String> createStories(List<JiraStory> jiraStories, JiraQueryResult epicInformation) throws IOException, InterruptedException {
        throw new UnsupportedOperationException();
    }

    public JiraQueryResult getStory(Jira jira, String username, char[] password) throws IOException, InterruptedException {
        String encodedAuth = getEncodeAuth(username, password);
        final String ticket = jira.getTicket();
        final HttpRequest request = createGetStoryRequest(encodedAuth, ticket);
        this.client.send(request, HttpResponse.BodyHandlers.ofString());
        return new JiraQueryResult();
    }

    private String getEncodeAuth(String username, char[] password) {
        final Base64.Encoder encoder = Base64.getEncoder();
        final String s = username + ":" + String.valueOf(password);
        final String result = encoder.encodeToString(s.getBytes());

        return result;
    }

    private HttpRequest createGetStoryRequest(String encodedAuth, String ticket) {
        return HttpRequest
                .newBuilder()
                .GET()
                .header("Content-Type", "application/json")
                .header("Authorization", "Basic " + encodedAuth)
                .uri(URI.create(baseUri + getStoryEndpoint + ticket))

                .build();
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
    String getGetStoryEndpoint() {
        return getStoryEndpoint;
    }

    @VisibleForTesting
    String getBulkCreateEndpoint() {
        return bulkCreateEndpoint;
    }
}
