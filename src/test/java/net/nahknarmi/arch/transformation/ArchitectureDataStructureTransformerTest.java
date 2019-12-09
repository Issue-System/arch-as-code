package net.nahknarmi.arch.transformation;

import com.structurizr.Workspace;
import net.nahknarmi.arch.model.ArchitectureDataStructure;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class ArchitectureDataStructureTransformerTest {
    private static final String PRODUCT_NAME = "DevSpaces";
    private static final String PRODUCT_DESCRIPTION = "DevSpaces is a tool";

    @Test
    public void should_transform_architecture_yaml_to_structurizr_workspace() throws IOException {
        ArchitectureDataStructure dataStructure = new ArchitectureDataStructure();
        dataStructure.setName(PRODUCT_NAME);
        dataStructure.setDescription(PRODUCT_DESCRIPTION);
        dataStructure.setId(1L);

        ArchitectureDataStructureTransformer architectureDataStructureTransformer = new ArchitectureDataStructureTransformer();
        System.err.println(architectureDataStructureTransformer);
        Workspace workspace = architectureDataStructureTransformer.toWorkSpace(dataStructure);

        assertNotNull(workspace);
        assertThat(workspace.getId(), equalTo(1L));
        assertThat(workspace.getName(), equalTo(PRODUCT_NAME));
        assertThat(workspace.getDescription(), equalTo(PRODUCT_DESCRIPTION));
        assertThat(workspace.getDocumentation().getSections().size(), equalTo(2));
    }


    //handle id being absent, name, description.

}
