package net.trilogy.arch.adapter.Jira;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class JiraCreateStoryStatus {
    private final boolean succeeded;
    private final String issueKey;
    private final String error;

    private JiraCreateStoryStatus(boolean succeeded, String issueKey, String error) {
        this.succeeded = succeeded;
        this.issueKey = issueKey;
        this.error = error;
    }

    public static JiraCreateStoryStatus failed(String error) {
        return new JiraCreateStoryStatus(false, null, error);
    }

    public static JiraCreateStoryStatus succeeded(String issueKey) {
        return new JiraCreateStoryStatus(true, issueKey, null);
    }

    public String toString() {
        return this.error;
    }
}
