package net.trilogy.arch.adapter.Jira;

import lombok.Getter;

@Getter
public class JiraQueryResult {
    private final String projectId;
    private final String projectKey;

    public JiraQueryResult(String projectId, String projectKey) {
        this.projectId = projectId;
        this.projectKey = projectKey;
    }
}
