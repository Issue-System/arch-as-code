package net.trilogy.arch.domain;

import net.trilogy.arch.ArchitectureDataStructureHelper;
import net.trilogy.arch.domain.diff.Diff;
import net.trilogy.arch.domain.diff.DiffSet;
import net.trilogy.arch.domain.diff.DiffableEntity;
import net.trilogy.arch.domain.diff.DiffableRelationship;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class DiffSetTest {
    @Test
    public void systemLevelDiffsShouldHaveSystemsAndPeople() {
        DiffSet diffset = getDiffSetWithAllTypesOfDiffs();
        assertThat(diffset.getSystemLevelDiffs(), equalTo(Set.of(getPersonDiff(), getSystemDiff())));
    }

    @Test
    public void systemLevelDiffsShouldHaveTheRelationshipsThatReferToSystemsOrPeople() {
        var relWithSource = getRelationshipDiff("1", getId(getSystemDiff()), "bleh");
        var relWithDestination = getRelationshipDiff("2", "bleh", getId(getPersonDiff()));
        var relWithBoth = getRelationshipDiff("3", getId(getPersonDiff()), getId(getSystemDiff()));

        var diffset = getDiffSetWithAllTypesOfDiffsPlus(relWithSource, relWithDestination, relWithBoth);

        assertThat(
                diffset.getSystemLevelDiffs(),
                equalTo(Set.of(getPersonDiff(), getSystemDiff(), relWithSource, relWithDestination, relWithBoth))
        );
    }

    @Test
    public void systemLevelDiffsShouldHaveTheEntitiesThatAreRelatedToSystemsOrPeople() {
        var container = getContainerDiff("container-id", "bleh");
        var component = getComponentDiff("component-id", "bleh");

        var relWithSource = getRelationshipDiff("1", getId(getSystemDiff()), "container-id");
        var relWithDestination = getRelationshipDiff("2", "component-id", getId(getPersonDiff()));

        var diffset = getDiffSetWithAllTypesOfDiffsPlus(container, component, relWithDestination, relWithSource);

        assertThat(
                diffset.getSystemLevelDiffs(),
                equalTo(Set.of(getPersonDiff(), getSystemDiff(), relWithSource, relWithDestination, container, component))
        );
    }

    private DiffSet getDiffSetWithAllTypesOfDiffs() {
        return new DiffSet(List.of(
                    getRelationshipDiff(),
                    getPersonDiff(),
                    getSystemDiff(),
                    getContainerDiff(),
                    getComponentDiff()
        ));
    }

    private DiffSet getDiffSetWithAllTypesOfDiffsPlus(Diff... additionalDiffs) {
        var diffs = new HashSet<>(Set.of(additionalDiffs));
        diffs.addAll(getDiffSetWithAllTypesOfDiffs().getDiffs());
        return new DiffSet(diffs);
    }

    private Diff getPersonDiff() {
        return getPersonDiff("p1");
    }
    private Diff getPersonDiff(String id) {
        return new Diff(
                new DiffableEntity(ArchitectureDataStructureHelper.createPerson(id)),
                null
        );
    }

    private Diff getComponentDiff() {
        return getComponentDiff("comp1", "comp1-system");
    }
    private Diff getComponentDiff(String id, String containerId) {
        return new Diff(
                new DiffableEntity(ArchitectureDataStructureHelper.createComponent(id, containerId)),
                null
        );
    }

    private Diff getContainerDiff() {
        return getContainerDiff("cont1", "cont1-system");
    }
    private Diff getContainerDiff(String id, String systemId) {
        return new Diff(
                new DiffableEntity(ArchitectureDataStructureHelper.createContainer(id, systemId)),
                null
        );
    }

    private Diff getSystemDiff() {
        return getSystemDiff("s1");
    }
    private Diff getSystemDiff(String id) {
        return new Diff(
                new DiffableEntity(ArchitectureDataStructureHelper.createSystem(id)),
                null
        );
    }

    private Diff getRelationshipDiff() {
        return getRelationshipDiff("r1", "r1-source", "r1-dest");
    }
    private Diff getRelationshipDiff(String id, String sourceId, String destinationId) {
        return new Diff(
                new DiffableRelationship(
                        sourceId,
                        ArchitectureDataStructureHelper.createRelationship(id, destinationId)
                ),
                null
        );
    }

    private String getId(Diff d) {
        return d.getElement().getId();
    }
}
