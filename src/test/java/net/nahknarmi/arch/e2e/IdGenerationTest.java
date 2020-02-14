package net.nahknarmi.arch.e2e;

import com.structurizr.Workspace;
import com.structurizr.model.Element;
import net.nahknarmi.arch.adapter.in.ArchitectureDataStructureReader;
import net.nahknarmi.arch.domain.ArchitectureDataStructure;
import net.nahknarmi.arch.transformation.ArchitectureDataStructureTransformer;
import net.nahknarmi.arch.transformation.TransformerFactory;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static net.nahknarmi.arch.TestHelper.TEST_PRODUCT_DOCUMENTATION_ROOT_PATH;
import static org.junit.Assert.assertEquals;

public class IdGenerationTest {

    @Test
    public void people_id_generation_test() throws IOException {
        ArchitectureDataStructure dataStructure = getDataStructure();
        Workspace workspace = getWorkspace(dataStructure);

        dataStructure.getModel().getPeople().forEach(p -> {
            String personId = p.getId();
            Element element = workspace.getModel().getElement(personId);
            String elementId = element.getId();

            assertEquals(personId, elementId);
        });
    }

    @Test
    public void systems_id_generation_test() throws IOException {
        ArchitectureDataStructure dataStructure = getDataStructure();
        Workspace workspace = getWorkspace(dataStructure);

        dataStructure.getModel().getSystems().forEach(s -> {
            String systemId = s.getId();
            Element element = workspace.getModel().getElement(systemId);
            String elementId = element.getId();

            assertEquals(systemId, elementId);
        });
    }

    @Test
    public void containers_id_generation_test() throws IOException {
        ArchitectureDataStructure dataStructure = getDataStructure();
        Workspace workspace = getWorkspace(dataStructure);

        dataStructure.getModel().getContainers().forEach(cont -> {
            String containerId = cont.getId();
            Element element = workspace.getModel().getElement(containerId);
            String elementId = element.getId();

            assertEquals(containerId, elementId);
        });
    }

    @Test
    public void component_id_generation_test() throws IOException {
        ArchitectureDataStructure dataStructure = getDataStructure();
        Workspace workspace = getWorkspace(dataStructure);

        dataStructure.getModel().getComponents().forEach(comp -> {
            String componentId = comp.getId();
            Element element = workspace.getModel().getElement(componentId);
            String elementId = element.getId();

            assertEquals(componentId, elementId);
        });
    }

    private ArchitectureDataStructure getDataStructure() throws IOException {
        File documentationRoot = new File(getClass().getResource(TEST_PRODUCT_DOCUMENTATION_ROOT_PATH).getPath());
        File manifestFile = new File(documentationRoot + File.separator + "data-structure.yml");
        return new ArchitectureDataStructureReader().load(manifestFile);
    }

    private Workspace getWorkspace(ArchitectureDataStructure dataStructure) {
        File documentationRoot = new File(getClass().getResource(TEST_PRODUCT_DOCUMENTATION_ROOT_PATH).getPath());
        ArchitectureDataStructureTransformer transformer = TransformerFactory.create(documentationRoot);
        return transformer.toWorkSpace(dataStructure);
    }
}
