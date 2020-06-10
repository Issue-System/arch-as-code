package net.trilogy.arch.domain.diff;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import net.trilogy.arch.domain.ArchitectureDataStructure;
import net.trilogy.arch.domain.c4.C4Relationship;
import net.trilogy.arch.domain.c4.Entity;

@EqualsAndHashCode
@RequiredArgsConstructor
public class DiffableRelationship implements Diffable {
    private final String sourceId;
    private final C4Relationship relationship;

    public DiffableRelationship(Entity entity, C4Relationship c4Relationship) {
        this.sourceId = entity.getId();
        this.relationship = c4Relationship;
    }

    public DiffableRelationship(ArchitectureDataStructure arch, C4Relationship c4Relationship) {
        final Entity source = arch.getModel()
                .allEntities()
                .stream()
                .filter(entity -> entity.getRelationships()
                        .stream()
                        .map(C4Relationship::getId)
                        .anyMatch(rId -> c4Relationship.getId().equals(rId)))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("No matching entity for relationship: " + c4Relationship.toString()));

        this.sourceId = source.getId();
        this.relationship = c4Relationship;
    }

    @Override
    public String getId() {
        return this.relationship.getId();
    }

    @Override
    public String getName() {
        return this.relationship.getDescription();
    }
}
