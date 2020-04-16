package net.trilogy.arch.adapter;

import net.trilogy.arch.TestHelper;
import net.trilogy.arch.adapter.in.WorkspaceReader;
import net.trilogy.arch.domain.ArchitectureDataStructure;
import net.trilogy.arch.domain.c4.*;
import net.trilogy.arch.domain.c4.view.C4DeploymentView;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Set;

import static net.trilogy.arch.domain.c4.C4Action.USES;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class WorkspaceReaderTest {

    @Test
    public void shouldHaveValidDescription() throws Exception {
        URL resource = getClass().getResource(TestHelper.JSON_STRUCTURIZR_EMPTY);
        ArchitectureDataStructure dataStructure = new WorkspaceReader().load(new File(resource.getPath()));

        assertThat(dataStructure.getDescription(), equalTo(""));
    }

    @Test
    public void shouldReadComponent() throws Exception {
        // TODO FUTURE: Probably a good idea to break out the giant .json into individual, small jsons per test with only what's needed.
        URL resource = getClass().getResource(TestHelper.JSON_STRUCTURIZR_THINK3_SOCOCO);
        ArchitectureDataStructure data = new WorkspaceReader().load(new File(resource.getPath()));

        C4Component component = (C4Component) data.getModel().findEntityById("220");

        assertThat(component.getName(), is(equalTo("Ionic")));
        assertThat(component.getContainerAlias(), is(nullValue()));
        assertThat(component.getAlias(), is(nullValue()));
        assertThat(component.getTags(), containsInAnyOrder(
                new C4Tag("Element"),
                new C4Tag("Component"),
                new C4Tag("External")
        ));
        assertThat(component.getDescription(), is(equalTo("Ionic native part for Android")));
        assertThat(component.getContainerId(), is(equalTo("219")));
        assertThat(component.getType(), is(equalTo(C4Type.component)));
        assertThat(component.getUrl(), is(nullValue()));
        assertThat(component.getPath(), is(C4Path.path("c4://Sococo Virtual Office/Android App/Ionic")));
        assertThat(component.getRelationships(), contains(
                new C4Relationship("239", null, USES, null, "16", "Runs", "Chromium")
        ));
        assertThat(component.getTechnology(), is(equalTo("Android")));
    }

    @Test
    public void shouldReadCorrectNumberOfElements() throws Exception {
        // TODO FUTURE: Probably a good idea to break out the giant .json into individual, small jsons per test with only what's needed.
        URL resource = getClass().getResource(TestHelper.JSON_STRUCTURIZR_THINK3_SOCOCO);
        ArchitectureDataStructure dataStructure = new WorkspaceReader().load(new File(resource.getPath()));

        assertThat(dataStructure.getName(), is(equalTo("Sococo Import")));
        assertThat(dataStructure.getBusinessUnit(), is(equalTo("Think3")));

        assertThat(dataStructure, is(notNullValue()));
        assertThat(dataStructure.getModel(), is(notNullValue()));
        MatcherAssert.assertThat(dataStructure.getModel().getPeople(), hasSize(7));
        MatcherAssert.assertThat(dataStructure.getModel().getSystems(), hasSize(13));
        MatcherAssert.assertThat(dataStructure.getModel().getContainers(), hasSize(28));
        MatcherAssert.assertThat(dataStructure.getModel().getComponents(), hasSize(35));
        MatcherAssert.assertThat(dataStructure.getModel().allRelationships(), hasSize(160));

        MatcherAssert.assertThat(dataStructure.getViews().getComponentViews(), hasSize(8));
        MatcherAssert.assertThat(dataStructure.getViews().getContainerViews(), hasSize(9));
        MatcherAssert.assertThat(dataStructure.getViews().getSystemViews(), hasSize(6));
    }

    @Test
    public void shouldReadDeploymentNodes() throws Exception {
        URL resource = getClass().getResource(TestHelper.JSON_STRUCTURIZR_BIG_BANK);
        ArchitectureDataStructure dataStructure = new WorkspaceReader().load(new File(resource.getPath()));

        assertThat(dataStructure.getModel().getDeploymentNodes().size(), is(equalTo(4)));
        assertThat(dataStructure.getModel().getDeploymentNodesRecursively().size(), is(equalTo(18)));

        var actual = (C4DeploymentNode) dataStructure.getModel().findEntityById("65");

        var expected = C4DeploymentNode.builder()
                .alias(null)
                .id("65")
                .name("Customer's computer")
                .children(List.of(
                        C4DeploymentNode.builder()
                                .id("66")
                                .name("Web Browser")
                                .environment("Live")
                                .technology("Chrome, Firefox, Safari, or Edge")
                                .instances(1)
                                .containerInstances(List.of(
                                        new C4ContainerInstance("67", "Live", new C4Reference("17", null), 2)
                                ))
                                .children(List.of())
                                .build()
                ))
                .containerInstances(List.of())
                .environment("Live")
                .technology("Microsoft Windows or Apple macOS")
                .instances(1)
                .tags(Set.of())
                .relationships(List.of())
                .build();

        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void shouldReadDeploymentViews() throws Exception {
        URL resource = getClass().getResource(TestHelper.JSON_STRUCTURIZR_BIG_BANK);
        ArchitectureDataStructure dataStructure = new WorkspaceReader().load(new File(resource.getPath()));
        C4DeploymentView actual = dataStructure.getViews().getDeploymentViews().stream()
                .filter(v -> v.getKey().equals("DevelopmentDeployment")).findAny().get();

        C4DeploymentView expected = new C4DeploymentView().builder()
                .key("DevelopmentDeployment")
                .name("Internet Banking System - Deployment - Development")
                .system(new C4Reference("2", null))
                .description("An example development deployment scenario for the Internet Banking System.")
                .environment("Development")
                .references(Set.of(new C4Reference("50", null)))
                .build();

        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void shouldReadDeploymentViewWithNoSystem() throws Exception {
        URL resource = getClass().getResource(TestHelper.JSON_STRUCTURIZR_NO_SYSTEM);
        final String key = "test";

        ArchitectureDataStructure dataStructure = new WorkspaceReader().load(new File(resource.getPath()));
        C4DeploymentView actual = dataStructure.getViews().getDeploymentViews().stream()
                .filter(v -> v.getKey().equals(key)).findAny().get();

        C4DeploymentView expected = new C4DeploymentView().builder()
                .key(key)
                .environment("Default")
                .description("")
                .name("Deployment - Default")
                .references(Set.of(new C4Reference("1", null)))
                .build();

        assertThat(actual, is(equalTo(expected)));
    }
}
