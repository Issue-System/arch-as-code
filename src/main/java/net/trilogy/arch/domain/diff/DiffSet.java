package net.trilogy.arch.domain.diff;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.trilogy.arch.domain.c4.C4Person;
import net.trilogy.arch.domain.c4.C4SoftwareSystem;
import net.trilogy.arch.domain.c4.C4Type;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EqualsAndHashCode
public class DiffSet {

    @Getter
    private final Set<Diff> diffs;

    public DiffSet(Collection<Diff> diffs) {
        this.diffs = new LinkedHashSet<>(diffs);
    }

    public Set<Diff> getSystemLevelDiffs() {
        var relationships = relationshipDiffs() .filter(relDiff -> {
                var element = ((DiffableRelationship) relDiff.getElement());
                return systemAndPeopleDiffs().anyMatch(it -> 
                        it.getElement().getId().equals(element.getSourceId()) ||
                        it.getElement().getId().equals(element.getRelationship().getWithId())
                );
        });

        return Stream.concat(systemAndPeopleDiffs(), relationships).collect(Collectors.toSet());
    }

    private Stream<Diff> relationshipDiffs() {
        return this.diffs.stream()
            .filter(diff -> C4Type.relationship.equals(diff.getElement().getType()));
    }

    private Stream<Diff> systemAndPeopleDiffs() {
        return this.diffs.stream()
            .filter(diff -> Set.of(C4Type.system, C4Type.person).contains(diff.getElement().getType()));
    }
}
