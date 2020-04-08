package net.trilogy.arch.domain.architectureUpdate;

import lombok.Builder;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode
public class MilestoneDependency {
    private final String description;
    private final List<Link> links;

    @Builder
    public MilestoneDependency(String description, List<Link> links) {
        this.description = description;
        this.links = links;
    }
}
