package net.nahknarmi.arch.adapter;

import com.structurizr.Workspace;
import com.structurizr.api.StructurizrClientException;
import net.nahknarmi.arch.model.ArchitectureDataStructure;
import net.nahknarmi.arch.model.ArchitectureDataStructureImporter;
import net.nahknarmi.arch.transformation.ArchitectureDataStructureTransformer;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class YAMLToStructurizrE2E {


    @Test
    public void should_be_able_to_submit_generated_workspace_to_structurizr_api_and_save_changes() throws IOException, StructurizrClientException {
        //given
        InputStream architectureManifestYaml =
                getClass().getResourceAsStream("/architecture/products/devspaces/dev-spaces-architecture.yml");

        //when
        //transform yaml file to workspace json
        ArchitectureDataStructureImporter importer = new ArchitectureDataStructureImporter();
        ArchitectureDataStructure dataStructure = importer.load(architectureManifestYaml);

        //then
        //load generated json to workspace
        Workspace workspace = new ArchitectureDataStructureTransformer().toWorkSpace(dataStructure);

        //submit json to struturizr
        new StructurizrAdapter(workspace.getId()).publish(workspace);

        //retrieve workspace from structurizr & ensure data was saved
        StructurizrAdapter adapter = new StructurizrAdapter(workspace.getId());
        assertThat(adapter.workspace().getDocumentation().getSections().size(), equalTo(2));
    }
}
