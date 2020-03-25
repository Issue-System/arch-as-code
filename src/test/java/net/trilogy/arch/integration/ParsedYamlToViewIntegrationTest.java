package net.trilogy.arch.integration;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.structurizr.Workspace;
import com.structurizr.view.*;
import net.trilogy.arch.TestHelper;
import net.trilogy.arch.adapter.in.ArchitectureDataStructureReader;
import net.trilogy.arch.domain.ArchitectureDataStructure;
import net.trilogy.arch.transformation.ArchitectureDataStructureTransformer;
import net.trilogy.arch.transformation.TransformerFactory;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

public class ParsedYamlToViewIntegrationTest {

    @Test
    public void system_view_test() throws IOException {
        Workspace workspace = getWorkspace();
        Collection<SystemContextView> systemContextViews = workspace.getViews().getSystemContextViews();
        SystemContextView view = systemContextViews.stream().findFirst().get();
        List<String> elementNames = view.getElements().stream().map(e -> e.getElement().getName()).collect(Collectors.toList());

        assertThat(view.getRelationships().size(), equalTo(4));
        assertThat(elementNames,
                containsInAnyOrder("Personal Banking Customer",
                        "E-mail System",
                        "Mainframe Banking System",
                        "Internet Banking System"
                ));
    }

    @Test
    public void container_view_test() throws IOException {
        Workspace workspace = getWorkspace();
        Collection<ContainerView> containerViews = workspace.getViews().getContainerViews();
        ContainerView view = containerViews.stream().findFirst().get();
        List<String> elementNames = view.getElements().stream().map(e -> e.getElement().getName()).collect(Collectors.toList());

        assertThat(view.getRelationships().size(), equalTo(10));
        assertThat(elementNames,
                containsInAnyOrder("Personal Banking Customer",
                        "E-mail System",
                        "Mainframe Banking System",
                        "Internet Banking System/API Application",
                        "Internet Banking System/Database",
                        "Internet Banking System/Web Application",
                        "Internet Banking System/Single-Page Application",
                        "Internet Banking System/Mobile App"
                ));
    }

    @Test
    public void component_view_test() throws IOException {
        Workspace workspace = getWorkspace();
        Collection<ComponentView> componentViews = workspace.getViews().getComponentViews();
        ComponentView view = componentViews.stream().findFirst().get();
        List<String> elementNames = view.getElements().stream().map(e -> e.getElement().getName()).collect(Collectors.toList());

        assertThat(view.getRelationships().size(), equalTo(13));
        assertThat(elementNames,
                containsInAnyOrder("E-mail System",
                        "Mainframe Banking System",
                        "Internet Banking System/Database",
                        "Internet Banking System/Single-Page Application",
                        "Internet Banking System/Mobile App",
                        "Internet Banking System/API Application/Sign In Controller",
                        "Internet Banking System/API Application/Reset Password Controller",
                        "Internet Banking System/API Application/Accounts Summary Controller",
                        "Internet Banking System/API Application/Mainframe Banking System Facade",
                        "Internet Banking System/API Application/E-mail Component",
                        "Internet Banking System/API Application/Security Component"
                ));
    }

    @Test
    public void deployment_view_test() throws IOException {
        Workspace workspace = getWorkspace();
        Collection<DeploymentView> deploymentViews = workspace.getViews().getDeploymentViews();
        DeploymentView view = deploymentViews.stream().findFirst().get();

        List<String> elementNames = view.getElements().stream().map(e -> e.getElement().getName()).collect(Collectors.toList());
        Iterables.removeIf(elementNames, Predicates.isNull());

        Set<RelationshipView> relationships = view.getRelationships();
        assertThat(relationships.size(), equalTo(3));

        assertThat(relationships.stream().map(r -> r.getId()).collect(Collectors.toList()),
                containsInAnyOrder(
                        "58->60",
                        "60->57",
                        "57->53")
        );
        assertThat(elementNames,
                containsInAnyOrder("Apache Tomcat",
                        "Web Browser",
                        "Developer Laptop",
                        "Docker Container - Database Server",
                        "Database Server",
                        "Docker Container - Web Server"
                ));
    }

    private Workspace getWorkspace() throws IOException {
        File documentationRoot = new File(getClass().getResource(TestHelper.ROOT_PATH_TO_TEST_VIEWS).getPath());
        File manifestFile = new File(documentationRoot + File.separator + "data-structure.yml");

        ArchitectureDataStructure dataStructure = new ArchitectureDataStructureReader().load(manifestFile);
        ArchitectureDataStructureTransformer transformer = TransformerFactory.create(documentationRoot);
        return transformer.toWorkSpace(dataStructure);
    }
}
