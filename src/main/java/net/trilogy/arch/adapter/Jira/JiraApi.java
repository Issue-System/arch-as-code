package net.trilogy.arch.adapter.Jira;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    public void createStories(List<JiraStory> jiraStories, String project_id, String projectKey, String username, char[] password) throws CreateStoriesException {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.noBody())
                .header("Authorization", "Basic " + getEncodeAuth(username, password))
                .header("Content-Type", "application/json")
                .uri(URI.create(baseUri + bulkCreateEndpoint))
                .build();
        try {
            this.client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new CreateStoriesException(e);
        }
    }

    public JiraQueryResult getStory(Jira jira, String username, char[] password) throws GetStoryException {
        String encodedAuth = getEncodeAuth(username, password);
        final String ticket = jira.getTicket();
        final HttpRequest request = createGetStoryRequest(encodedAuth, ticket);
        try {
            HttpResponse<String> response = this.client.send(request, HttpResponse.BodyHandlers.ofString());
            return parseResponse(response);
        } catch (Exception e) {
            throw new GetStoryException(e);
        }
    }

    private JiraQueryResult parseResponse(HttpResponse<String> response) throws JsonProcessingException {
        JsonNode json = new ObjectMapper().readValue(response.body(), JsonNode.class);
        String projectId = json.get("fields").get("project").get("id").asText();
        String projectKey = json.get("fields").get("project").get("key").asText();
        return new JiraQueryResult(projectId, projectKey);
    }

    private String getEncodeAuth(String username, char[] password) {
        final String s = username + ":" + String.valueOf(password);
        return Base64.getEncoder().encodeToString(s.getBytes());
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

    public static class GetStoryException extends Exception {
        public GetStoryException(Throwable cause) { super(cause); }
    }
    public static class CreateStoriesException extends Exception {
        public CreateStoriesException(Throwable cause) { super(cause); }
    }
}
