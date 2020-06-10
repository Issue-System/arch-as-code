package net.trilogy.arch.domain.diff;

public interface Diffable {
    String getId();
    String getName();
    boolean equals(Object o);
}
