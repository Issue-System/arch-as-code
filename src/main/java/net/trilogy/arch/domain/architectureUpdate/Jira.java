package net.trilogy.arch.domain.architectureUpdate;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Jira {
    private final String ticket;
    private final String link;

    public Jira(String ticket, String link) {
        this.ticket = ticket;
        this.link = link;
    }

    static Jira blank() {
        return new Jira("[SAMPLE JIRA TICKET]", "[SAMPLE JIRA TICKET LINK]");
    }
}
