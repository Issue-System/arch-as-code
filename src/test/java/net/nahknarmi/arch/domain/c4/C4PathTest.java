package net.nahknarmi.arch.domain.c4;

import com.structurizr.Workspace;
import com.structurizr.model.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


public class C4PathTest {
    private Workspace workspace;

    @Before
    public void setUp() {
        workspace = new Workspace("foo", "blah");
    }

    @Test
    public void buildPath_for_system() {
        SoftwareSystem element = buildSoftwareSystem();
        buildPath(element, C4Type.system, "c4://system");
    }

    @Test
    public void buildPath_for_person() {
        Person element = workspace.getModel().addPerson("person", "bar");
        buildPath(element, C4Type.person, "@person");
    }

    @Test
    public void buildPath_for_container() {
        Container container = buildContainer();
        buildPath(container, C4Type.container, "c4://system/container");
    }

    @Test
    public void buildPath_for_component() {
        Component component = buildComponent("component");
        buildPath(component, C4Type.component, "c4://system/container/component");
    }

    @Test
    public void buildPath_for_component_with_slash() {
        Component component = buildComponent("component/abc");
        buildPath(component, C4Type.component, "c4://system/container/component-abc");
    }

    private Component buildComponent(String name) {
        Container container = buildContainer();
        return container.addComponent(name, "bar");
    }

    private Container buildContainer() {
        SoftwareSystem softwareSystem = buildSoftwareSystem();
        return softwareSystem.addContainer("container", "bar", "bazz");
    }

    private SoftwareSystem buildSoftwareSystem() {
        return workspace.getModel().addSoftwareSystem("system", "bar");
    }

    private void buildPath(Element element, C4Type expectedType, String expectedPath) {
        C4Path path = C4Path.buildPath(element);
        assertThat(path.getType(), equalTo(expectedType));
        assertThat(path.getPath(), equalTo(expectedPath));
    }

    @Test
    public void person() {
        C4Path path = new C4Path("@PCA");

        assertThat(path.getName(), equalTo("PCA"));
        assertThat(path.getType(), equalTo(C4Type.person));
    }

    @Test
    public void system() {
        C4Path path = new C4Path("c4://DevSpaces");

        assertThat(path.getName(), equalTo("DevSpaces"));
        assertThat(path.getSystemName(), equalTo("DevSpaces"));
        assertThat(path.getType(), equalTo(C4Type.system));
    }

    @Test
    public void container() {
        C4Path path = new C4Path("c4://DevSpaces/DevSpaces API");

        assertThat(path.getName(), equalTo("DevSpaces API"));
        assertThat(path.getSystemName(), equalTo("DevSpaces"));
        assertThat(path.getContainerName(), equalTo(Optional.of("DevSpaces API")));

        assertThat(path.getType(), equalTo(C4Type.container));
    }

    @Test
    public void component() {
        C4Path path = new C4Path("c4://DevSpaces/DevSpaces API/Sign-In Component");

        assertThat(path.getName(), equalTo("Sign-In Component"));
        assertThat(path.getSystemName(), equalTo("DevSpaces"));
        assertThat(path.getContainerName(), equalTo(Optional.of("DevSpaces API")));
        assertThat(path.getComponentName(), equalTo(Optional.of("Sign-In Component")));

        assertThat(path.getType(), equalTo(C4Type.component));
    }

    @Test(expected = IllegalArgumentException.class)
    public void missing_person() {
        new C4Path("@");
    }

    @Test(expected = IllegalArgumentException.class)
    public void missing_system() {
        new C4Path("c4://");
    }
}
