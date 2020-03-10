package net.trilogy.arch.domain.c4;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Optional.ofNullable;

@Data
@NoArgsConstructor
public abstract class BaseEntity implements Entity {
    @NonNull
    protected String id;
    protected String alias;
    protected C4Path path;
    @NonNull
    protected String name;
    protected String description;
    protected Set<C4Tag> tags = emptySet();
    protected List<C4Relationship> relationships = emptyList();

    public BaseEntity(@NonNull String id, String alias, C4Path path, @NonNull String name, String description, Set<C4Tag> tags, List<C4Relationship> relationships) {
        this.id = id;
        this.alias = alias;
        this.path = path;
        this.description = description;
        this.tags = ofNullable(tags).orElse(emptySet());
        this.relationships = ofNullable(relationships).orElse(emptyList());
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass() != getClass()) {
            return false;
        }
        return new EqualsBuilder().append(this.getId(), ((BaseEntity) o).getId()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getId()).toHashCode();
    }
}
