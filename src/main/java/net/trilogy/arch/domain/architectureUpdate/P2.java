package net.trilogy.arch.domain.architectureUpdate;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class P2 {
    private final String link;
    private final Jira jira;

    @Builder
    public P2(String link, Jira jira) {
        this.link = link;
        this.jira = jira;
    }
}
