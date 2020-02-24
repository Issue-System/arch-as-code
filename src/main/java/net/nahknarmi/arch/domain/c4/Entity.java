package net.nahknarmi.arch.domain.c4;

import java.util.List;

public interface Entity extends HasRelation, HasTag, HasIdentity {
    String getId();

    String getAlias();

    String getName();

    String getDescription();

    C4Type getType();

    C4Path getPath();

    List<C4Relationship> getRelationships();
}
