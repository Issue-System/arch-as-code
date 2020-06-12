package net.trilogy.arch.domain.diff;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.trilogy.arch.domain.c4.C4SoftwareSystem;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

@EqualsAndHashCode
public class DiffSet {

    @Getter
    private final Set<Diff> diffs;

    public DiffSet(Collection<Diff> diffs) {
        this.diffs = new LinkedHashSet<>(diffs);
    }

    public Set<Diff> GetSystemLevelDiffs() {
        diffs.stream()
                .filter(d -> d.getElement() instanceof C4SoftwareSystem);
        return Set.of();
    }
}
