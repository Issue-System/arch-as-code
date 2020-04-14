package net.trilogy.arch.adapter.Jira;

import com.google.common.annotations.VisibleForTesting;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class JiraApi {
    private HttpClient client;
    public static String JIRA_BASE_URI = "http://jira.devfactory.com/rest/api/2";
    public static String BULK_ENDPOINT = "/issue/bulk";


    public JiraApi(HttpClient client) {
        this.client = client;
    }

    public void createStory() throws IOException, InterruptedException {
        final String uri = JIRA_BASE_URI + BULK_ENDPOINT;
        String body = "{\n" +
                "    \"issueUpdates\": [\n" +
                "        {\n" +
                "            \"fields\": {\n" +
                "                \"project\": {\n" +
                "                    \"id\": \"43900\"\n" +
                "                },\n" +
                "                \"summary\": \"something's very wrong\"\n" +
                "            }\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        final HttpRequest request = HttpRequest
                .newBuilder()
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(URI.create(uri))
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @VisibleForTesting
    HttpClient getHttpClient() {
        return client;
    }
}
