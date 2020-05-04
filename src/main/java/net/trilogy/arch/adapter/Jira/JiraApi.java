package net.trilogy.arch.adapter.Jira;

import com.google.common.annotations.VisibleForTesting;
import net.trilogy.arch.domain.architectureUpdate.Jira;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

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

    public JiraQueryResult getStory(Jira jira, String username, char[] password) throws IOException, InterruptedException {
        String encodedAuth = getEncodeAuth(username, password);
        final HttpRequest request = createGetStoryRequest(encodedAuth);
        this.client.send(request, HttpResponse.BodyHandlers.ofString());
        return new JiraQueryResult();
    }

    private String getEncodeAuth(String username, char[] password) {
        final Base64.Encoder encoder = Base64.getEncoder();
        final String s = username + ":" + String.valueOf(password);
        final String result = encoder.encodeToString(s.getBytes());

        return result;
    }

    private HttpRequest createGetStoryRequest(String encodedAuth) {
        return HttpRequest
                .newBuilder()
                .GET()
                .header("Content-Type", "application/json")
                .header("Authorization", "Basic " + encodedAuth)
                .uri(URI.create("http://localhost"))
                .build();
    }

    //    public String
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
