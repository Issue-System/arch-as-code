package net.trilogy.arch.adapter.Jira;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import net.trilogy.arch.adapter.FilesFacade;

import java.io.IOException;
import java.net.http.HttpClient;
import java.nio.file.Path;

public class JiraApiFactory {
    public static final String JIRA_API_SETTINGS_FILE_PATH = ".arch-as-code/jira/settings.json";

    private HttpClient client;
    private String baseUri;
    private String bulkCreateEndpoint;

    public JiraApiFactory(FilesFacade files) throws IOException {
        var rawContents = files.readString(Path.of(JIRA_API_SETTINGS_FILE_PATH));
        final ObjectMapper objectMapper = new ObjectMapper();
        this.baseUri = objectMapper.readTree(rawContents).get("base_uri").textValue();
        this.bulkCreateEndpoint = objectMapper.readTree(rawContents).get("bulk_create_endpoint").textValue();
        this.client = createClient();
    }

    public JiraApi create() {
        return new JiraApi(this.client, this.baseUri, this.bulkCreateEndpoint);
    }

    private HttpClient createClient() {
        if (client == null) {
            client = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();
        }

        return client;
    }

    @VisibleForTesting
    public HttpClient getClient() {
        return client;
    }
}
