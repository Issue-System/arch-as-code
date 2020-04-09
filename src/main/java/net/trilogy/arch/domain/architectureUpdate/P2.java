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

    static P2 blank() {
        return new P2("[SAMPLE LINK TO P1]", Jira.blank());
    }
}
