package net.trilogy.arch.domain;

import net.trilogy.arch.ArchitectureDataStructureHelper;
import net.trilogy.arch.TestHelper;
import net.trilogy.arch.adapter.structurizr.WorkspaceReader;
import net.trilogy.arch.domain.c4.C4Person;
import net.trilogy.arch.domain.c4.C4SoftwareSystem;
import net.trilogy.arch.domain.diff.Diff;
import net.trilogy.arch.domain.diff.DiffSet;
import net.trilogy.arch.domain.diff.DiffableEntity;
import net.trilogy.arch.domain.diff.DiffableRelationship;
import net.trilogy.arch.services.ArchitectureDiffCalculator;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.contains;

public class DiffSetTest {

    @Test
    public void systemLevelDiffsShouldHaveTheRightEntities() throws Exception {
        DiffSet diffset = getDiffSetWithAllTypesOfDiffs();
        assertThat(diffset.getSystemLevelDiffs(), contains(getPersonDiff(), getSystemDiff()));
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
}
