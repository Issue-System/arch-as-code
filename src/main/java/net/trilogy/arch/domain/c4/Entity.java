package net.trilogy.arch.domain.c4;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Optional.ofNullable;

@Data
@NoArgsConstructor
public abstract class Entity implements HasRelation, HasTag, HasIdentity, Comparable<Entity> {
    @NonNull
    protected String id;
    protected String alias;
    protected C4Path path;
    @NonNull
    protected String name;
    protected String description;
    protected Set<C4Tag> tags = emptySet();
    protected Set<C4Relationship> relationships = emptySet();

    abstract public Entity shallowCopy();

    abstract public C4Type getType();

    @Override
    public int compareTo(Entity other) {
        return this.getId().compareTo(other.getId());
    }

    public Entity(@NonNull String id, String alias, C4Path path, @NonNull String name, String description, Set<C4Tag> tags, Set<C4Relationship> relationships) {
        this.id = id;
        this.alias = alias;
        this.path = path;
        this.description = description;
        this.tags = ofNullable(tags).orElse(emptySet());
        this.relationships = ofNullable(relationships).orElse(emptySet());
        this.name = name;
    }
}
