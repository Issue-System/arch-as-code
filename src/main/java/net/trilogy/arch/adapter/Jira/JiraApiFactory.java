package net.trilogy.arch.adapter.Jira;

import com.google.common.annotations.VisibleForTesting;

import java.net.http.HttpClient;

public class JiraApiFactory {
    public static final String JIRA_API_SETTINGS_FILE_PATH = ".arch-as-code/jira/settings.json";

    private HttpClient build;

    public JiraApi create() {
        return new JiraApi(createClient());
    }

    @VisibleForTesting
    HttpClient createClient() {
        if (build == null) {
            build = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();
        }

        return build;
    }
}
