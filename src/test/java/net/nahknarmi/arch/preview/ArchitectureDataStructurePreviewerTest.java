package net.nahknarmi.arch.preview;

import com.structurizr.Workspace;
import net.nahknarmi.arch.domain.ArchitectureDataStructure;
import net.nahknarmi.arch.domain.ArchitectureDataStructureReader;
import net.nahknarmi.arch.transformation.TransformerFactory;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static net.nahknarmi.arch.TestHelper.TEST_PRODUCT_DOCUMENTATION_ROOT_PATH;
import static net.nahknarmi.arch.TestHelper.TEST_SPACES_MANIFEST_PATH;

public class ArchitectureDataStructurePreviewerTest {

    @Test
    public void preview() throws IOException {
        File productDocumentationRoot = new File(getClass().getResource(TEST_PRODUCT_DOCUMENTATION_ROOT_PATH).getPath());
        File manifestPath = new File(getClass().getResource(TEST_SPACES_MANIFEST_PATH).getPath());
        ArchitectureDataStructure dataStructure = new ArchitectureDataStructureReader().load(manifestPath);
        Workspace workspace = TransformerFactory.create(productDocumentationRoot).toWorkSpace(dataStructure);

        new ArchitectureDataStructurePreviewer().preview(workspace);
    }
}