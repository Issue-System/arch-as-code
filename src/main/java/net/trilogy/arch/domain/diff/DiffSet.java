package net.trilogy.arch.domain.diff;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.trilogy.arch.domain.c4.C4Type;

@EqualsAndHashCode
public class DiffSet {

    @Getter
    private final Set<Diff> diffs;

    public DiffSet(Collection<Diff> diffs) {
        this.diffs = new LinkedHashSet<>(diffs);
    }

    public Set<Diff> getSystemLevelDiffs() {
        var systemsAndPeople = findSystemAndPeopleDiffs();
        var relationships = findRelationshipsAmong(systemsAndPeople);
        return setOf(systemsAndPeople, relationships);
    }

    @SafeVarargs
    private <T> Set<T> setOf(Collection<T>... itemCollections) {
        return Stream.of(itemCollections)
            .flatMap(it -> it.stream())
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
                return Stream.of(rel.getSourceId(), rel.getRelationship().getWithId());
            })
            .flatMap(id -> findById(id).stream())
            .collect(Collectors.toList());
    }

    private List<Diff> findRelationshipsAmong(Collection<Diff> entityDiffs) {
        return relationshipDiffs()
            .stream()
            .filter(relDiff -> {
                var element = ((DiffableRelationship) relDiff.getElement());
                return entityDiffs.stream().anyMatch(it -> 
                    it.getElement().getId().equals(element.getSourceId())
                );
            })
            .filter(relDiff -> {
                var element = ((DiffableRelationship) relDiff.getElement());
                return entityDiffs.stream().anyMatch(it -> 
                    it.getElement().getId().equals(element.getRelationship().getWithId())
                );
            })
            .collect(Collectors.toList());
    }

    private List<Diff> findRelationshipsThatReferToAnyOf(Collection<Diff> entityDiffs) {
        return relationshipDiffs()
            .stream()
            .filter(relDiff -> {
                var element = ((DiffableRelationship) relDiff.getElement());
                return entityDiffs.stream().anyMatch(it -> 
                        it.getElement().getId().equals(element.getSourceId()) ||
                        it.getElement().getId().equals(element.getRelationship().getWithId())
                );
            })
            .collect(Collectors.toList());
    }

    private List<Diff> relationshipDiffs() {
        return this.diffs.stream()
            .filter(diff -> C4Type.relationship.equals(diff.getElement().getType()))
            .collect(Collectors.toList());
    }

    private List<Diff> findSystemAndPeopleDiffs() {
        return this.diffs.stream()
            .filter(diff -> Set.of(C4Type.system, C4Type.person).contains(diff.getElement().getType()))
            .collect(Collectors.toList());
    }
}
