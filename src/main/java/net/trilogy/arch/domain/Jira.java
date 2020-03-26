package net.trilogy.arch.domain;

public class Jira {
    private final String ticket;
    private final String link;

    public Jira(String ticket, String link) {
        this.ticket = ticket;
        this.link = link;
    }
}
