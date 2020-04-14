package net.trilogy.arch.adapter.Jira;

import com.google.common.annotations.VisibleForTesting;

import java.net.http.HttpClient;

public class JiraApiFactory {

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
