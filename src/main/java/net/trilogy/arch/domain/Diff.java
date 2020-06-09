package net.trilogy.arch.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.trilogy.arch.domain.c4.C4Relationship;
import net.trilogy.arch.domain.c4.Entity;

import java.util.Set;
import java.util.stream.Stream;

import com.google.common.collect.Sets;

@Getter
@EqualsAndHashCode
public class Diff {
    final private Diffable before;
    final private Diffable after;
    final private Set<? extends Diffable> descendantsBefore;
    final private Set<? extends Diffable> descendantsAfter;
    final private Status status;

    public Diff(Diffable before, Diffable after) {
        this.before = before;
        this.after = after;
        this.descendantsAfter = Set.of();
        this.descendantsBefore = Set.of();
        this.status = calculateStatus();
    }

    public String toString() {
        // TODO: Temporary solution until we play diagramming card
        String marker;
        if (status.equals(Status.UPDATED)) marker = "*";
        else if (status.equals(Status.NO_UPDATE_BUT_CHILDREN_UPDATED)) marker = "~";
        else if (status.equals(Status.CREATED)) marker = "+";
        else if (status.equals(Status.DELETED)) marker = "-";
        else marker = "=";

        String id;
        if (before == null) {
            id = after.getId();
        } else {
            id = before.getId();
        }

        return marker + id;
    }

    public Diff(Diffable before, Set<? extends Diffable> descendantsBefore, Diffable after, Set<? extends Diffable> descendantsAfter) {
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

    public String toDot(Set<Diff> diffs) {
        if(this.after != null) { 
            return toDot(this.after, diffs);
        }
        if(this.before != null) { 
            return toDot(this.before, diffs);
        }
        return "";
    }

    private String toDot(Diffable thing, Set<Diff> diffs) {
        if(thing instanceof Entity) {
            return ((Entity) thing).getName();
        }
        if(thing instanceof C4Relationship) {
            final var rel = (C4Relationship) thing;

            final var to = diffs.stream()
                .flatMap(it -> Stream.of(it.getBefore(), it.getAfter()))
                .filter(it -> it != null)
                .filter(it -> it.getId().equals(rel.getWithId()))
                .findAny()
                .map(it -> ((Entity) it).getName())
                .orElse("UNKNOWN");

            String from = "UNKNOWN";
            for(var diff : diffs) {
                if(
                    diff.getDescendantsAfter().stream().filter(it -> it.getId().equals(thing.getId())).count()
                    > 0
                ) {
                    from = ((Entity) diff.getAfter()).getName();
                    break;
                }
                if(
                    diff.getDescendantsBefore().stream().filter(it -> it.getId().equals(thing.getId())).count()
                    > 0
                ) {
                    from = ((Entity) diff.getBefore()).getName();
                    break;
                }
            }

            return from + " -> " + to;
        }
        return "";
    }
}
