package net.nahknarmi.arch.domain.c4;

import java.util.List;

public interface Relatable {
    String getName();

    List<RelationshipPair> relations();

    String getDescription();
}
