package net.nahknarmi.arch.domain.c4;

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
public abstract class BaseEntity implements Entity {
    @NonNull
    protected String id;
    protected String alias;
    protected C4Path path;
    @NonNull
    protected String name;
    @NonNull
    protected String description;
    protected Set<C4Tag> tags = emptySet();
    protected List<C4Relationship> relationships = emptyList();

    public BaseEntity(@NonNull String id, String alias, C4Path path, @NonNull String name, @NonNull String description, Set<C4Tag> tags, List<C4Relationship> relationships) {
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
        if (this == o) return true;
        if (!(o instanceof BaseEntity)) return false;
        if (!super.equals(o)) return false;

        BaseEntity that = (BaseEntity) o;

        return getPath() != null ? getPath().equals(that.getPath()) : that.getPath() == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getPath() != null ? getPath().hashCode() : 0);
        return result;
    }

    @Override
    public Entity getReferenced(C4Model dataStructureModel) {
        Entity result;
        if (id != null) {
            result = dataStructureModel.findEntityById(id);
        } else if (alias != null) {
            result = dataStructureModel.findEntityByAlias(alias);
        } else {
            throw new IllegalStateException("Entity is missing id and alias: " + this);
        }

        return result;
    }
}
