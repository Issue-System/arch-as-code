package net.trilogy.arch.domain.diff;

import net.trilogy.arch.domain.c4.C4Type;

public interface Diffable {
    C4Type getType();
    String getId();
    String getName();
    boolean equals(Object o);
}
