package net.trilogy.arch.domain.diff;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.trilogy.arch.domain.c4.C4Person;
import net.trilogy.arch.domain.c4.C4SoftwareSystem;
import net.trilogy.arch.domain.c4.C4Type;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Sets;

@EqualsAndHashCode
public class DiffSet {

    @Getter
    private final Set<Diff> diffs;

    public DiffSet(Collection<Diff> diffs) {
        this.diffs = new LinkedHashSet<>(diffs);
    }

    public Set<Diff> getSystemLevelDiffs() {
        var systemsAndPeople = systemAndPeopleDiffs();
        var relationships = relationshipsThatReferToAnyOf(systemsAndPeople);

        var related = relationships.stream()
            .flatMap(r -> {
                var rel = (DiffableRelationship) r.getElement();
                return Stream.of(rel.getSourceId(), rel.getRelationship().getWithId());
            })
            .flatMap(id -> byId(id).stream())
            .collect(Collectors.toSet());

        return setOf(systemsAndPeople, relationships, related);
    }

    @SafeVarargs
    private <T> Set<T> setOf(Collection<T>... itemCollections) {
        return Stream.of(itemCollections)
            .flatMap(it -> it.stream())
            .collect(Collectors.toSet());
    }

    private List<Diff> byId(String id) {
        return this.diffs.stream()
            .filter(it -> it.getElement().getId().equals(id))
            .collect(Collectors.toList());
    }

    private List<Diff> relationshipsThatReferToAnyOf(Collection<Diff> diffs) {
        return relationshipDiffs()
            .stream()
            .filter(relDiff -> {
                var element = ((DiffableRelationship) relDiff.getElement());
                return diffs.stream().anyMatch(it -> 
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

    private List<Diff> systemAndPeopleDiffs() {
        return this.diffs.stream()
            .filter(diff -> Set.of(C4Type.system, C4Type.person).contains(diff.getElement().getType()))
            .collect(Collectors.toList());
    }
}
