package net.trilogy.arch.domain.diff;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.trilogy.arch.domain.c4.C4Type;
import net.trilogy.arch.domain.c4.Entity;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class DiffableEntity implements Diffable {

    @Getter private final Entity entity;

    @Override
    public String getId() {
        return entity.getId();
    }

    @Override
    public String getName() {
        return entity.getName();
    }

    @Override
    public boolean equals(final Object otherObject) {
        if (!(otherObject instanceof DiffableEntity)) return false;
        final var other = (DiffableEntity) otherObject;

        if (Objects.equals(entity, other.entity)) return true;
        if (Objects.isNull(entity) != Objects.isNull(other.entity)) return false;

        var a = entity.shallowCopy();
        var b = other.entity.shallowCopy();
        a.setRelationships(List.of());
        b.setRelationships(List.of());
        return a.equals(b);
    }

    @Override
    public C4Type getType() {
        return this.getEntity().getType();
    }

    @Override
    public int hashCode() {
        var dup = this.entity.shallowCopy();
        dup.setRelationships(List.of());
        return dup.hashCode();
    }
}
