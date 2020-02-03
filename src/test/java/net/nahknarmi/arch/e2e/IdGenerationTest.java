package net.nahknarmi.arch.e2e;

import com.structurizr.Workspace;
import com.structurizr.model.Element;
import net.nahknarmi.arch.domain.ArchitectureDataStructure;
import net.nahknarmi.arch.domain.ArchitectureDataStructureReader;
import net.nahknarmi.arch.transformation.ArchitectureDataStructureTransformer;
import net.nahknarmi.arch.transformation.TransformerFactory;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static net.nahknarmi.arch.TestHelper.TEST_PRODUCT_DOCUMENTATION_ROOT_PATH;
import static org.hamcrest.MatcherAssert.assertThat;

public class IdGenerationTest {

    @Test
    public void id_generation_test() throws IOException {
        Workspace workspace = getWorkspace();
//        Element element = workspace.getModel().getElement("1");
        String devspaces_cli_id = "35da62ec7ce608f9e335cfc8abefd22277808158f07c0fb5a19db1760ab42ce8";// DevSpaces CLI (container id)
        System.out.println("!");

//        assertThat(workspace.);
    }

    private Workspace getWorkspace() throws IOException {
        File documentationRoot = new File(getClass().getResource(TEST_PRODUCT_DOCUMENTATION_ROOT_PATH).getPath());
        File manifestFile = new File(documentationRoot + File.separator + "data-structure.yml");

        ArchitectureDataStructure dataStructure = new ArchitectureDataStructureReader().load(manifestFile);
        ArchitectureDataStructureTransformer transformer = TransformerFactory.create(documentationRoot);
        return transformer.toWorkSpace(dataStructure);
    }
}
