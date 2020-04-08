package net.trilogy.arch.domain.architectureUpdate;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Link {
    private final String description;
    private final String link;

    public Link(String description, String link) {
        this.description = description;
        this.link = link;
    }
}
