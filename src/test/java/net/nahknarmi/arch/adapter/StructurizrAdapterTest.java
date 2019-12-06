package net.nahknarmi.arch.adapter;


import com.structurizr.Workspace;
import com.structurizr.api.StructurizrClientException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class StructurizrAdapterTest {

    @Test
    public void should_bump_structurizr_revision_after_publishing() throws StructurizrClientException {
        int workspaceId = 49328;

        StructurizrAdapter adapter = StructurizrAdapter.load(workspaceId);
        Workspace workspace = adapter.workspace();
        Long revision = workspace.getRevision();

        //when
        adapter.publish();

        //then
        Workspace updatedWorkspace = StructurizrAdapter.load(workspaceId).workspace();
        assertThat(updatedWorkspace.getRevision(), is(equalTo(revision + 1)));
    }
}