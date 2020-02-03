package net.nahknarmi.arch.domain.c4;

import com.structurizr.Workspace;
import net.nahknarmi.arch.domain.ArchitectureDataStructure;
import net.nahknarmi.arch.domain.ArchitectureDataStructureReader;
import net.nahknarmi.arch.transformation.ArchitectureDataStructureTransformer;
import net.nahknarmi.arch.transformation.TransformerFactory;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static net.nahknarmi.arch.TestHelper.TEST_PRODUCT_DOCUMENTATION_ROOT_PATH;

public class C4ModelTest {

    @Test
    public void person_getEntityFromElement() throws IOException {
        Workspace workspace = getWorkspace();
        workspace.getModel();

    }


    @Test
    public void system_getEntityFromElement() {
    }

    @Test
    public void container_getEntityFromElement() {
    }

    @Test
    public void component_getEntityFromElement() {
    }

    private Workspace getWorkspace() throws IOException {
        GetModel getModel = new GetModel().invoke();
        File documentationRoot = getModel.getDocumentationRoot();
        ArchitectureDataStructure dataStructure = getModel.getDataStructure();
        ArchitectureDataStructureTransformer transformer = TransformerFactory.create(documentationRoot);
        return transformer.toWorkSpace(dataStructure);
    }

    private class GetModel {
        private File documentationRoot;
        private ArchitectureDataStructure dataStructure;

        public File getDocumentationRoot() {
            return documentationRoot;
        }

        public ArchitectureDataStructure getDataStructure() {
            return dataStructure;
        }

        public GetModel invoke() throws IOException {
            documentationRoot = new File(getClass().getResource(TEST_PRODUCT_DOCUMENTATION_ROOT_PATH).getPath());
            File manifestFile = new File(documentationRoot + File.separator + "data-structure.yml");

            dataStructure = new ArchitectureDataStructureReader().load(manifestFile);
            return this;
        }
    }
}
