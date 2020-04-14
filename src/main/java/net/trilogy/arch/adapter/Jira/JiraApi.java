package net.trilogy.arch.adapter.Jira;

import com.google.common.annotations.VisibleForTesting;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class JiraApi {
    private HttpClient client;
    private String uri;


    public JiraApi(HttpClient client) {
        this.client = client;
    }

    public void createStory() throws IOException, InterruptedException {
        uri = "http://www.example.com";
        final HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(uri))
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @VisibleForTesting
    HttpClient getHttpClient() {
        return client;
    }
}
