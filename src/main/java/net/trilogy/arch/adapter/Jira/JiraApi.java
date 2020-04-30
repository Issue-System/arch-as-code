package net.trilogy.arch.adapter.Jira;

import com.google.common.annotations.VisibleForTesting;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class JiraApi {
    private final HttpClient client;

    public JiraApi(HttpClient client) {
        this.client = client;
    }

    public HttpResponse<String> createStory() throws IOException, InterruptedException {
        String username = Files.readString(Paths.get("/tmp/arch-as-code-secret/username.txt")).trim();
        String password = Files.readString(Paths.get("/tmp/arch-as-code-secret/password.txt")).trim();
        String stakeholder_name = Files.readString(Paths.get("/tmp/arch-as-code-secret/stakeholder_name.txt")).trim();
        String stakeholder_key = Files.readString(Paths.get("/tmp/arch-as-code-secret/stakeholder_key.txt")).trim();
        String stakeholder_email = Files.readString(Paths.get("/tmp/arch-as-code-secret/stakeholder_email.txt")).trim();
        String jira_project_id = Files.readString(Paths.get("/tmp/arch-as-code-secret/jira_project_id.txt")).trim();
        String issue_type_id = Files.readString(Paths.get("/tmp/arch-as-code-secret/issue_type_id.txt")).trim();

        String jira_base_uri = Files.readString(Paths.get("/tmp/arch-as-code-secret/jira_base_uri.txt")).trim();
        String bulk_endpoint = Files.readString(Paths.get("/tmp/arch-as-code-secret/bulk_endpoint.txt")).trim();

        Base64.Encoder encoder = Base64.getEncoder();
        String encodedAuth = encoder.encodeToString((username + ":" + password).getBytes());

        final HttpRequest request = HttpRequest
                .newBuilder()
                .header("Content-Type", "application/json")
                .header("Authorization", "Basic " + encodedAuth)
                .POST(HttpRequest.BodyPublishers.ofString(buildBody(stakeholder_email, stakeholder_key, stakeholder_name, jira_project_id, jira_base_uri, issue_type_id)))
                .uri(URI.create(jira_base_uri + bulk_endpoint))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private String buildBody(String stakeholder_email, String stakeholder_key, String stakeholder_name, String jira_project_id, String jira_base_uri, String issue_type_id) {
        return ""
                + "{                                                                                                      "
                + "  \"issueUpdates\": [                                                                                  "
                + "    {                                                                                                  "
                + "      \"fields\": {                                                                                    "
                + "        \"project\": {                                                                                 "
                + "          \"id\": \"" + jira_project_id + "\"                                                          "
                + "        },                                                                                             "
                + "        \"summary\": \"Hello\",                                                                        "
                + "        \"customfield_10401\": {                                                                       "
                + "           \"self\": \""+jira_base_uri+"/user?username="+stakeholder_key+"\",                          "
                + "           \"name\": \""+stakeholder_key+"\",                                                          "
                + "           \"key\": \""+stakeholder_key+"\",                                                           "
                + "           \"emailAddress\": \""+stakeholder_email+"\",                                                "
                + "           \"displayName\": \""+stakeholder_name+"\",                                                  "
                + "           \"active\": true,                                                                           "
                + "           \"timeZone\": \"UTC\"                                                                       "
                + "        },                                                                                             "
                + "        \"issuetype\": {                                                                               "
                + "          \"id\": \"" + issue_type_id + "\"                                                            "
                + "        },                                                                                             "
                + "        \"customfield_10405\": \"https://github.com/trilogy-group/arch-as-code/\",                     "
                + "        \"description\": \"NA\",                                                                       "
                + "        \"customfield_10004\": \"Testing Jira Integration\"                                            "
                + "      }                                                                                                "
                + "    }                                                                                                  "
                + "  ]                                                                                                    "
                + "}                                                                                                      "
                + "";
    }

    @VisibleForTesting
    HttpClient getHttpClient() {
        return client;
    }
}
