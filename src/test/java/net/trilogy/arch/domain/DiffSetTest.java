package net.trilogy.arch.domain;

import static org.junit.Assert.fail;

import org.junit.Ignore;
import org.junit.Test;

public class DiffSetTest{

    @Ignore("TODO")
    @Test
    public void shouldGetSystemLevelDiffs() {
        // This should be multiple tests
        
        // given: a diffset containing various types of diffs
        
        // when: diffset.getSystemLevelDiffs() is called
        
        // then: it should return only the diffs that satisfy the following criteria:
        //  EITHER
        //  - diff must be of type DiffableEntity
        //  - the diffableEntity.getEntity() must be one of the allowed types (C4Person, C4SoftwareSystem)
        
        //  OR
        //  - diff must be of type DiffableRelationship
        //  - one of [ DiffableRelationship.getSourceId() , DiffableRelationship.getRelationship().getWithId() ] matches the id of a DiffableEntity that is returned by the previous set of criteria
        
        //  OR
        //  - diff must be of type DiffableEntity 
        //  - the diffableEntity.getId() matches one of [ DiffableRelationship.getSourceId() , DiffableRelationship.getRelationship().getWithId() ] of a DiffableRelationship that is returned by the previous set of criteria.
        fail("WIP");
    }
}
