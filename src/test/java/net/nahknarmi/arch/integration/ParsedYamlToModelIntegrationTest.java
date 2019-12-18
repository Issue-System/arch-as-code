package net.nahknarmi.arch.integration;

import com.structurizr.Workspace;
import com.structurizr.model.*;
import net.nahknarmi.arch.domain.ArchitectureDataStructure;
import net.nahknarmi.arch.domain.ArchitectureDataStructureReader;
import net.nahknarmi.arch.transformation.ArchitectureDataStructureTransformer;
import net.nahknarmi.arch.transformation.TransformerFactory;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertTrue;
import static net.nahknarmi.arch.TestHelper.PRODUCT_NAME;
import static net.nahknarmi.arch.TestHelper.TEST_PRODUCT_DOCUMENTATION_ROOT_PATH;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;

public class ParsedYamlToModelIntegrationTest {

    @Test
    public void should_build_person_developer() throws FileNotFoundException {
        String personName = "Developer";
        Workspace workspace = getWorkspace();

        Person person = workspace.getModel().getPersonWithName(personName);
        Set<Relationship> relationships = person.getRelationships();
        List<String> relationshipNames = relationships.stream().map(r -> r.getDestination().getName()).collect(Collectors.toList());

        assertThat(relationships, hasSize(3));
        assertTrue(relationshipNames.contains("GitHub"));
        assertTrue(relationshipNames.contains("DevSpaces CLI"));
        assertTrue(relationshipNames.contains("SaasOps"));

        assertEquals(person.getDescription(), "Developer building software");
    }

    @Test
    public void should_build_person_saasops() throws FileNotFoundException {
        String personName = "SaasOps";
        Workspace workspace = getWorkspace();

        Person person = workspace.getModel().getPersonWithName(personName);

        assertEquals(person.getDescription(), "SaasOps operating system");
    }

    @Test
    public void should_build_system_github() throws FileNotFoundException {
        String systemName = "GitHub";
        Workspace workspace = getWorkspace();

        SoftwareSystem system = workspace.getModel().getSoftwareSystemWithName(systemName);

        assertEquals(system.getDescription(), "Hosts code and used for identity management");
    }

    @Test
    public void should_build_system_devspaces_cli() throws FileNotFoundException {
        String systemName = "DevSpaces CLI";
        Workspace workspace = getWorkspace();

        SoftwareSystem system = workspace.getModel().getSoftwareSystemWithName(systemName);
        Set<Relationship> relationships = system.getRelationships();
        List<String> relationshipNames = relationships.stream().map(r -> r.getDestination().getName()).collect(Collectors.toList());

        assertThat(relationships, hasSize(1));
        assertTrue(relationshipNames.contains("DevSpaces Backend"));

        assertEquals(system.getDescription(), "Command Line Interface for interacting with DevSpaces Backend");
    }

    @Test
    public void should_build_system_devspaces_backend() throws FileNotFoundException {
        String systemName = "DevSpaces Backend";
        Workspace workspace = getWorkspace();

        SoftwareSystem system = workspace.getModel().getSoftwareSystemWithName(systemName);
        Set<Container> containers = system.getContainers();
        List<String> containerNames = system.getContainers().stream().map(c -> c.getName()).collect(Collectors.toList());

        assertThat(containers, hasSize(2));
        assertTrue(containerNames.contains("DevSpaces Web Application"));
        assertTrue(containerNames.contains("DevSpaces API"));

        assertEquals(system.getDescription(), "Restful API providing capabilities for interacting with a DevSpace");
    }

    @Test
    public void should_build_container_devspaces_web_application() throws FileNotFoundException {
        String systemName = "DevSpaces Backend";
        String containerName = "DevSpaces Web Application";
        Workspace workspace = getWorkspace();

        SoftwareSystem system = workspace.getModel().getSoftwareSystemWithName(systemName);
        Container container = system.getContainerWithName(containerName);

        Set<Relationship> relationships = container.getRelationships();
        List<String> relationshipNames = relationships.stream().map(r -> r.getDestination().getName()).collect(Collectors.toList());

        assertThat(relationships, hasSize(1));
        assertTrue(relationshipNames.contains("DevSpaces API"));

        assertEquals(container.getDescription(), "Manage dev spaces");
        assertEquals(container.getTechnology(), "Angular");
    }

    private Workspace getWorkspace() throws FileNotFoundException {
        File documentationRoot = new File(getClass().getResource(TEST_PRODUCT_DOCUMENTATION_ROOT_PATH).getPath());
        File manifestFile = new File(documentationRoot + File.separator + PRODUCT_NAME.toLowerCase() + File.separator + "data-structure.yml");

        ArchitectureDataStructure dataStructure = new ArchitectureDataStructureReader().load(manifestFile);
        ArchitectureDataStructureTransformer transformer = TransformerFactory.create(documentationRoot);
        return transformer.toWorkSpace(dataStructure);
    }
}
