package net.trilogy.arch.domain;

import net.trilogy.arch.ArchitectureDataStructureHelper;
import net.trilogy.arch.TestHelper;
import net.trilogy.arch.adapter.structurizr.WorkspaceReader;
import net.trilogy.arch.domain.c4.C4Person;
import net.trilogy.arch.domain.c4.C4SoftwareSystem;
import net.trilogy.arch.domain.diff.Diff;
import net.trilogy.arch.domain.diff.DiffSet;
import net.trilogy.arch.services.ArchitectureDiffCalculator;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class DiffSetTest {

    @Ignore("WIP")
    @Test
    public void shouldGetSystemLevelDiffs() throws Exception {
        // given: a diffset containing various types of diffs
        DiffSet diffSet = getDiffSet();

        // when: diffset.getSystemLevelDiffs() is called
        Set<Diff> topLevel = diffSet.GetSystemLevelDiffs();

        // then: it should return only the diffs that satisfy the following criteria:
        //  EITHER
        //  - diff must be of type DiffableEntity
        //  - the diffableEntity.getEntity() must be one of the allowed types (C4Person, C4SoftwareSystem)
        final var result = topLevel.stream()
                .map(diff -> diff.getClass())
                .collect(Collectors.toSet());

        System.out.println(result);

        assertThat(
                result,
                equalTo(Set.of(C4Person.class, C4SoftwareSystem.class)))
        ;

        //  OR
        //  - diff must be of type DiffableRelationship
        //  - one of [ DiffableRelationship.getSourceId() , DiffableRelationship.getRelationship().getWithId() ] matches
        //    the id of a DiffableEntity that is returned by the previous set of criteria

        //  OR
        //  - diff must be of type DiffableEntity
        //  - the diffableEntity.getId() matches one of [ DiffableRelationship.getSourceId() , DiffableRelationship.getRelationship().getWithId() ]
        //    of a DiffableRelationship that is returned by the previous set of criteria.

    }

    private DiffSet getDiffSet() throws Exception {
        URL resource = getClass().getResource(TestHelper.JSON_STRUCTURIZR_BIG_BANK);
        final ArchitectureDataStructure before = ArchitectureDataStructureHelper.emptyArch().build();
        final ArchitectureDataStructure after = new WorkspaceReader().load(new File(resource.getPath()));

        return ArchitectureDiffCalculator.diff(before, after);
    }
}
