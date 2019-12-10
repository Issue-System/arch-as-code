package net.nahknarmi.arch.adapter;


import com.structurizr.Workspace;
import com.structurizr.api.StructurizrClientException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class StructurizrAdapterTest {
    private int WORKSPACE_ID = 49344;

    @Test
    public void should_bump_structurizr_revision_after_publishing() throws StructurizrClientException {
        StructurizrAdapter adapter = new StructurizrAdapter(WORKSPACE_ID);
        Workspace workspace = adapter.workspace();
        Long revision = workspace.getRevision();

        //when
        adapter.publish(workspace);

        //then
        Workspace updatedWorkspace = new StructurizrAdapter(WORKSPACE_ID).workspace();
        assertThat(updatedWorkspace.getRevision(), is(equalTo(revision + 1)));
    }

}
