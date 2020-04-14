package net.trilogy.arch.domain.architectureUpdate;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Epic {
    private final String title;
    private final Jira jira;

    @Builder
    public Epic(String title, Jira jira) {
        this.title = title;
        this.jira = jira;
    }

    public static Epic blank() {
        return new Epic("[SAMPLE EPIC TITLE]", Jira.blank());
    }
}
