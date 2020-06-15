package net.trilogy.arch.domain;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

import net.trilogy.arch.ArchitectureDataStructureHelper;
import net.trilogy.arch.domain.diff.Diff;
import net.trilogy.arch.domain.diff.DiffSet;
import net.trilogy.arch.domain.diff.DiffableEntity;
import net.trilogy.arch.domain.diff.DiffableRelationship;

public class DiffSetTest {
    @Test
    public void systemLevelDiffsShouldHaveSystemsAndPeople() throws Exception {
        DiffSet diffset = getDiffSetWithAllTypesOfDiffs();
        assertThat(diffset.getSystemLevelDiffs(), equalTo(Set.of(getPersonDiff(), getSystemDiff())));
    }

    @Test
    public void systemLevelDiffsShouldHaveTheRelationshipsThatReferToSystemsOrPeople() throws Exception {
        var relWithSource = new Diff(
                null,
                createRelationship("1", getId(getPersonDiff()), "bleh")
        );
        var relWithDestination = new Diff(
                createRelationship("2", "bleh", getId(getSystemDiff())),
                null
        );
        var relWithBoth = new Diff(
                createRelationship("2", getId(getPersonDiff()), getId(getSystemDiff())),
                null
        );

        var diffset = getDiffSetWithAllTypesOfDiffsPlus(relWithSource, relWithDestination, relWithBoth);

        assertThat(
                diffset.getSystemLevelDiffs(),
                equalTo(Set.of(getPersonDiff(), getSystemDiff(), relWithSource, relWithDestination, relWithBoth))
        );
    }

    @Ignore("TODO")
    @Test
    public void systemLevelDiffsShouldHaveTheEntitiesThatAreRelatedToSystemsOrPeople() throws Exception {
        fail("WIP");
    }

        //  EITHER
        //  - diff must be of type DiffableEntity
        //  - the diffableEntity.getEntity() must be one of the allowed types (C4Person, C4SoftwareSystem)
        //  OR
        //  - diff must be of type DiffableRelationship
        //  - one of [ DiffableRelationship.getSourceId() , DiffableRelationship.getRelationship().getWithId() ] matches
        //    the id of a DiffableEntity that is returned by the previous set of criteria
        //  OR
        //  - diff must be of type DiffableEntity
        //  - the diffableEntity.getId() matches one of [ DiffableRelationship.getSourceId() , DiffableRelationship.getRelationship().getWithId() ]
        //    of a DiffableRelationship that is returned by the previous set of criteria.

    private DiffSet getDiffSetWithAllTypesOfDiffs() throws Exception {
        return new DiffSet(List.of(
                    getRelationshipDiff(),
                    getPersonDiff(),
                    getSystemDiff(),
                    getContainerDiff(),
                    getComponentDiff()
        ));
    }

    private DiffSet getDiffSetWithAllTypesOfDiffsPlus(Diff... additionalDiffs) throws Exception {
        var diffs = new HashSet<Diff>(Set.of(additionalDiffs));
        diffs.addAll(getDiffSetWithAllTypesOfDiffs().getDiffs());
        return new DiffSet(diffs);
    }

    private Diff getPersonDiff() {
        return new Diff(
                new DiffableEntity(ArchitectureDataStructureHelper.createPerson("p1")),
                null
        );
    }

    private Diff getComponentDiff() {
        return new Diff(
                new DiffableEntity(ArchitectureDataStructureHelper.createComponent("comp1", "comp1-container")),
                null
        );
    }

    private Diff getContainerDiff() {
        return new Diff(
                new DiffableEntity(ArchitectureDataStructureHelper.createContainer("cont1", "cont1-system")),
                null
        );
    }

    private Diff getSystemDiff() {
        return new Diff(
                new DiffableEntity(ArchitectureDataStructureHelper.createSystem("s1")),
                null
        );
    }

    private Diff getRelationshipDiff() {
        return new Diff(
                new DiffableRelationship("r1-source", ArchitectureDataStructureHelper.createRelationship("r1", "r1-dest")),
                null
        );
    }

    private DiffableRelationship createRelationship(String id, String source, String destination) {
        return new DiffableRelationship(source, ArchitectureDataStructureHelper.createRelationship(id, destination));
    }

    private String getId(Diff d){
        return d.getElement().getId();
    }
}
