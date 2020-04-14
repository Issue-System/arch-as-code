package net.trilogy.arch.adapter.Jira;

import com.google.common.annotations.VisibleForTesting;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class JiraApi {
    public static String JIRA_BASE_URI = "http://jira.devfactory.com/rest/api/2";
    public static String BULK_ENDPOINT = "/issue/bulk";
    public static String JIRA_PROJECT_ID = "43900";
    public static String ISSUE_TYPE_ID = "10000";

    private HttpClient client;

    public JiraApi(HttpClient client) {
        this.client = client;
    }

    public HttpResponse<String> createStory() throws IOException, InterruptedException {
        final HttpRequest request = HttpRequest
                .newBuilder()
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(buildBody()))
                .uri(URI.create(JIRA_BASE_URI + BULK_ENDPOINT))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private String buildBody() {
        return ""
                + "{                                                             "
                + "  \"issueUpdates\": [                                         "
                + "    {                                                         "
                + "      \"fields\": {                                           "
                + "        \"project\": {                                        "
                + "          \"id\": \"" + JIRA_PROJECT_ID + "\"                 "
                + "        },                                                    "
                + "        \"issuetype\": {                                      "
                + "          \"id\": \"" + ISSUE_TYPE_ID + "\"                   "
                + "        }                                                     "
                + "      }                                                       "
                + "    }                                                         "
                + "  ]                                                           "
                + "}                                                             "
                + "";
    }

    @VisibleForTesting
    HttpClient getHttpClient() {
        return client;
    }
}
