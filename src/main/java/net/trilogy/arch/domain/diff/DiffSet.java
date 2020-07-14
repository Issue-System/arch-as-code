package net.trilogy.arch.domain.diff;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.trilogy.arch.domain.c4.C4Component;
import net.trilogy.arch.domain.c4.C4Container;
import net.trilogy.arch.domain.c4.C4Type;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EqualsAndHashCode
public class DiffSet {

    @Getter
    private final Set<Diff> diffs;

    private final List<Diff> relationships;

    public DiffSet(Collection<Diff> diffs) {
        this.diffs = new LinkedHashSet<>(diffs);
        this.relationships = this.diffs.stream()
                .filter(diff -> C4Type.RELATIONSHIP.equals(diff.getElement().getType()))
                .collect(Collectors.toList());
    }

    public Set<Diff> getSystemLevelDiffs() {
        var systemsAndPeople = this.diffs.stream()
                .filter(diff -> Set.of(C4Type.SYSTEM, C4Type.PERSON).contains(diff.getElement().getType()))
                .collect(Collectors.toList());

        var relationships = findRelationshipsThatReferToAnyOf(systemsAndPeople).stream()
                .filter(r -> doesRelationshipRefersToOneOfTypes(r, Set.of(C4Type.PERSON, C4Type.SYSTEM)))
                .collect(Collectors.toSet());
        return setOf(systemsAndPeople, relationships);
    }

    public Set<Diff> getContainerLevelDiffs(String systemId) {
        var containers = this.diffs.stream()
                .filter(diff -> diff.getElement().getType().equals(C4Type.CONTAINER))
                .filter(diff -> ((C4Container) ((DiffableEntity) diff.getElement()).getEntity()).getSystemId().equals(systemId))
                .collect(Collectors.toSet());

        var relationships = findRelationshipsThatReferToAnyOf(containers).stream()
                .filter(r -> doesRelationshipRefersToOneOfTypes(r, Set.of(C4Type.PERSON, C4Type.SYSTEM, C4Type.CONTAINER)))
                .collect(Collectors.toSet());
        var otherRelatedEntities = findDiffsReferredToBy(relationships);

        return setOf(containers, relationships, otherRelatedEntities);
    }

    public Set<Diff> getComponentLevelDiffs(String containerId) {
        var containers = this.diffs.stream()
                .filter(diff -> diff.getElement().getType().equals(C4Type.COMPONENT))
                .filter(diff -> ((C4Component) ((DiffableEntity) diff.getElement()).getEntity()).getContainerId().equals(containerId))
                .collect(Collectors.toSet());

        var relationships = findRelationshipsThatReferToAnyOf(containers);
        var otherRelatedEntities = findDiffsReferredToBy(relationships);
        return setOf(containers, relationships, otherRelatedEntities);
    }

    @SafeVarargs
    private <T> Set<T> setOf(Collection<T>... itemCollections) {
        return Stream.of(itemCollections)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    private List<Diff> findById(String id) {
        return this.diffs.stream()
                .filter(it -> it.getElement().getId().equals(id))
                .collect(Collectors.toList());
    }

    private List<Diff> findDiffsReferredToBy(Collection<Diff> relationshipDiffs) {
        return relationshipDiffs.stream()
                .flatMap(r -> {
                    var rel = (DiffableRelationship) r.getElement();
                    return Stream.of(rel.getSourceId(), rel.getDestinationId());
                })
                .flatMap(id -> findById(id).stream())
                .collect(Collectors.toList());
    }

    private List<Diff> findRelationshipsThatReferToAnyOf(Collection<Diff> entityDiffs) {
        return relationships
                .stream()
                .filter(relDiff -> {
                    var element = ((DiffableRelationship) relDiff.getElement());
                    return entityDiffs.stream().anyMatch(it ->
                            it.getElement().getId().equals(element.getSourceId()) ||
                                    it.getElement().getId().equals(element.getDestinationId())
                    );
                })
                .collect(Collectors.toList());
    }

    private boolean doesRelationshipRefersToOneOfTypes(Diff relationshipDiff, Set<C4Type> types) {
        final String sourceId = ((DiffableRelationship) relationshipDiff.getElement()).getSourceId();
        final String destId = ((DiffableRelationship) relationshipDiff.getElement()).getDestinationId();

        return findById(sourceId).stream().anyMatch(it -> types.contains(it.getElement().getType())) &&
                findById(destId).stream().anyMatch(it -> types.contains(it.getElement().getType()));
    }
}
