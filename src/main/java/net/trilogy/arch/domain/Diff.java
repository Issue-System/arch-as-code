package net.trilogy.arch.domain;

import java.util.Set;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class Diff{
    final private Diffable<?> before;
    final private Diffable<?> after;
    final private Set<? extends Diffable<?>> descendantsBefore;
    final private Set<? extends Diffable<?>> descendantsAfter;
    private Status status;

    @Deprecated
    public Diff(Diffable<?> before, Diffable<?> after) {
        this.before = before;
        this.after = after;
        this.descendantsAfter = Set.of();
        this.descendantsBefore = Set.of();
        this.status = calculateStatus();
    }

    public Diff(Diffable<?> before, Set<? extends Diffable<?>> descendantsBefore, Diffable<?> after, Set<? extends Diffable<?>> descendantsAfter) {
        this.before = before;
        this.after = after;
        this.descendantsAfter = descendantsBefore;
        this.descendantsBefore = descendantsAfter;
        this.status = calculateStatus();
    }

    private Status calculateStatus() {
        if (before == null && after == null) throw new UnsupportedOperationException();
        if (before == null) return Status.CREATED;
        if (after == null) return Status.DELETED;
        if (!before.equals(after)) return Status.UPDATED;
        if (!descendantsBefore.equals(descendantsAfter)) return Status.NO_UPDATE_BUT_CHILDREN_UPDATED;
        return Status.NO_UPDATE;
    }

    public enum Status {
        CREATED,
        UPDATED,
        DELETED,
        NO_UPDATE_BUT_CHILDREN_UPDATED,
        NO_UPDATE
    }
}
