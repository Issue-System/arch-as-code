package net.trilogy.arch.domain;

import org.junit.Test;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.Set;

public class DiffTest {

    @Test
    public void shouldCalculateCreatedStatus() {
        final Diff diff = new Diff(
                null, Set.of(),
                new Thing("toBeCreated"), Set.of()
        );
        assertThat(diff.getStatus(), equalTo(Diff.Status.CREATED));
    }

    @Test
    public void shouldCalculateDeletedStatus() {
        final Diff diff = new Diff(
                new Thing("toBeDeleted"), Set.of(),
                null, Set.of()
        );
        assertThat(diff.getStatus(), equalTo(Diff.Status.DELETED));
    }

    @Test
    public void shouldCalculateUpdatedStatus() {
        final Diff diff = new Diff(
                new Thing("toBeUpdated"), Set.of(),
                new Thing("updated"), Set.of()
        );
        assertThat(diff.getStatus(), equalTo(Diff.Status.UPDATED));
    }

    @Test
    public void shouldCalculateNoUpdateStatus() {
        final Diff diff = new Diff(
                new Thing("noChange"), Set.of(),
                new Thing("noChange"), Set.of()
        );
        assertThat(diff.getStatus(), equalTo(Diff.Status.NO_UPDATE));
    }

    @Test
    public void shouldCalculatedChildrenUpdated() {
        final Diff diff = new Diff(
                new Thing("noUpdate"), Set.of(new Thing("a")),
                new Thing("noUpdate"), Set.of(new Thing("b"))
        );
        assertThat(diff.getStatus(), equalTo(Diff.Status.NO_UPDATE_BUT_CHILDREN_UPDATED));
    }

    @Test
    public void shouldNotCalculateChildrenIfChangedStatus() {
        final Diff created = new Diff(
                null, Set.of(new Thing("a")),
                new Thing("toBeCreated"), Set.of(new Thing("b"))
        );
        final Diff deleted = new Diff(
                new Thing("toBeDeleted"), Set.of(new Thing("a")),
                null, Set.of(new Thing("b"))
        );
        final Diff updated = new Diff(
                new Thing("toBeUpdated"), Set.of(new Thing("a")),
                new Thing("updated"), Set.of(new Thing("b"))
        );

        assertThat(created.getStatus(), equalTo(Diff.Status.CREATED));
        assertThat(deleted.getStatus(), equalTo(Diff.Status.DELETED));
        assertThat(updated.getStatus(), equalTo(Diff.Status.UPDATED));
    }

    @EqualsAndHashCode
    @Getter
    private static class Thing implements Diffable<Thing> {
        private final String id;
        public Thing(String id) { this.id = id; }
        public Thing shallowCopy() { return this; }
    }
}
