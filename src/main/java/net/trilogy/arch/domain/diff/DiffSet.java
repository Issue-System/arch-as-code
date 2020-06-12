package net.trilogy.arch.domain.diff;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class DiffSet {

    @Getter private final Set<Diff> diffs;

    public DiffSet(Collection<Diff> diffs) {
        this.diffs = diffs.stream().collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
