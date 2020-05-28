package net.trilogy.arch.adapter.jira;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class JiraCreateStoryStatus {
    private final boolean succeeded;
    private final String issueKey;
    private final String issueLink;
    private final String error;

    private JiraCreateStoryStatus(boolean succeeded, String issueKey, String issueLink, String error) {
        this.succeeded = succeeded;
        this.issueKey = issueKey;
        this.issueLink = issueLink;
        this.error = error;
    }

    public static JiraCreateStoryStatus failed(String error) {
        return new JiraCreateStoryStatus(false, null, null, error);
    }

    public static JiraCreateStoryStatus succeeded(String issueKey, String issueLink) {
        return new JiraCreateStoryStatus(true, issueKey, issueLink, null);
    }
}
