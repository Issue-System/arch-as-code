package net.trilogy.arch.domain.c4;

import java.util.Set;

public interface HasRelation {
    Set<C4Relationship> getRelationships();
}
