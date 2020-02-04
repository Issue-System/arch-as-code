package net.nahknarmi.arch.e2e;

import com.structurizr.Workspace;
import net.nahknarmi.arch.domain.ArchitectureDataStructure;
import net.nahknarmi.arch.domain.ArchitectureDataStructureReader;
import net.nahknarmi.arch.domain.c4.Entity;
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

        workspace.getModel().getPeople().stream().forEach(p -> {
            String personId = p.getId();
            Entity entity = dataStructure.getModel().getByPath(personId);
            String pathString = entity.getPath().getPath();

            assertEquals(personId, pathString);
        });
    }

    @Test
    public void systems_id_generation_test() throws IOException {
        ArchitectureDataStructure dataStructure = getDataStructure();
        Workspace workspace = getWorkspace(dataStructure);

        workspace.getModel().getSoftwareSystems().stream().forEach(s -> {
            String systemId = s.getId();
            Entity entity = dataStructure.getModel().getByPath(systemId);
            String pathString = entity.getPath().getPath();

            assertEquals(systemId, pathString);
        });
    }

    @Test
    public void containers_id_generation_test() throws IOException {
        ArchitectureDataStructure dataStructure = getDataStructure();
        Workspace workspace = getWorkspace(dataStructure);

        workspace.getModel().getSoftwareSystems().stream().forEach(system -> {
            system.getContainers().stream().forEach(cont -> {
                String containerId = cont.getId();
                Entity entity = dataStructure.getModel().getByPath(containerId);
                String pathString = entity.getPath().getPath();

                assertEquals(containerId, pathString);
            });
        });
    }

    @Test
    public void component_id_generation_test() throws IOException {
        ArchitectureDataStructure dataStructure = getDataStructure();
        Workspace workspace = getWorkspace(dataStructure);

        workspace.getModel().getSoftwareSystems().stream().forEach(system -> {
            system.getContainers().stream().forEach(container -> {
                container.getComponents().stream().forEach(comp -> {
                    String componentId = comp.getId();
                    Entity entity = dataStructure.getModel().getByPath(componentId);
                    String pathString = entity.getPath().getPath();

                    assertEquals(componentId, pathString);
                });
            });
        });
    }

    private ArchitectureDataStructure getDataStructure() throws IOException {
        File documentationRoot = new File(getClass().getResource(TEST_PRODUCT_DOCUMENTATION_ROOT_PATH).getPath());
        File manifestFile = new File(documentationRoot + File.separator + "data-structure.yml");
        ArchitectureDataStructure dataStructure = new ArchitectureDataStructureReader().load(manifestFile);
        return dataStructure;
    }

    private Workspace getWorkspace(ArchitectureDataStructure dataStructure) throws IOException {
        File documentationRoot = new File(getClass().getResource(TEST_PRODUCT_DOCUMENTATION_ROOT_PATH).getPath());
        ArchitectureDataStructureTransformer transformer = TransformerFactory.create(documentationRoot);
        return transformer.toWorkSpace(dataStructure);
    }
}
