package net.nahknarmi.arch.adapter;


import com.structurizr.Workspace;
import com.structurizr.api.StructurizrClient;
import com.structurizr.api.StructurizrClientException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class StructurizrAdapterTest {
    private int WORKSPACE_ID = 49328;

    @Test
    public void should_bump_structurizr_revision_after_publishing() throws StructurizrClientException {


        StructurizrAdapter adapter = StructurizrAdapter.load(WORKSPACE_ID);
        Workspace workspace = adapter.workspace();
        Long revision = workspace.getRevision();

        //when
        adapter.publish();

        //then
        Workspace updatedWorkspace = StructurizrAdapter.load(WORKSPACE_ID).workspace();
        assertThat(updatedWorkspace.getRevision(), is(equalTo(revision + 1)));
    }


    @Test
    public void should_upload_project_from_json_file() throws Exception {
        StructurizrAdapter adapter = StructurizrAdapter.load(WORKSPACE_ID);
        adapter.upload();
    }
}