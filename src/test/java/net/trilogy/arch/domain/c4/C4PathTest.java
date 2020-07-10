package net.trilogy.arch.domain.c4;

import com.structurizr.Workspace;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.Person;
import com.structurizr.model.SoftwareSystem;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;


public class C4PathTest {
    @Rule
    public final ErrorCollector collector = new ErrorCollector();

    private Workspace workspace;

    @Before
    public void setUp() {
        workspace = new Workspace("foo", "blah");
    }

    @Test
    public void buildPath_for_system() {
        SoftwareSystem element = buildSoftwareSystem();

        C4Path path = C4Path.buildPath(element);

        collector.checkThat(path.type(), equalTo(C4Type.system));
        collector.checkThat(path.getPath(), equalTo("c4://system"));
    }

    @Test
    public void buildPath_for_person() {
        Person element = workspace.getModel().addPerson("person", "bar");

        C4Path path = C4Path.buildPath(element);

        collector.checkThat(path.type(), equalTo(C4Type.person));
        collector.checkThat(path.getPath(), equalTo("@person"));
    }

    @Test
    public void buildPath_for_container() {
        Container container = buildContainer();

        C4Path path = C4Path.buildPath(container);

        collector.checkThat(path.type(), equalTo(C4Type.container));
        collector.checkThat(path.getPath(), equalTo("c4://system/container"));
    }

    @Test
    public void buildPath_for_component() {
        Component component = buildComponent("component");

        C4Path path = C4Path.buildPath(component);

        collector.checkThat(path.type(), equalTo(C4Type.component));
        collector.checkThat(path.getPath(), equalTo("c4://system/container/component"));
    }

    @Test
    public void buildPath_for_component_with_dot() {
        Component component = buildComponent("Sococo.App");

        C4Path path = C4Path.buildPath(component);

        collector.checkThat(path.type(), equalTo(C4Type.component));
        collector.checkThat(path.getPath(), equalTo("c4://system/container/Sococo.App"));
    }

    @Test
    public void buildPath_for_component_with_slash() {
        Component component = buildComponent("component/abc");

        C4Path path = C4Path.buildPath(component);

        collector.checkThat(path.type(), equalTo(C4Type.component));
        collector.checkThat(path.getPath(), equalTo("c4://system/container/component-abc"));
    }

    @Test
    public void person() {
        C4Path path = C4Path.path("@PCA");

        collector.checkThat(path.name(), equalTo("PCA"));
        collector.checkThat(path.type(), equalTo(C4Type.person));
    }

    @Test
    public void system() {
        C4Path path = C4Path.path("c4://DevSpaces");

        collector.checkThat(path.name(), equalTo("DevSpaces"));
        collector.checkThat(path.systemName(), equalTo("DevSpaces"));
        collector.checkThat(path.type(), equalTo(C4Type.system));
    }

    @Test
    public void container() {
        C4Path path = C4Path.path("c4://DevSpaces/DevSpaces API");

        collector.checkThat(path.name(), equalTo("DevSpaces API"));
        collector.checkThat(path.systemName(), equalTo("DevSpaces"));
        collector.checkThat(path.containerName(), equalTo(Optional.of("DevSpaces API")));

        collector.checkThat(path.type(), equalTo(C4Type.container));
    }

    @Test
    public void component() {
        C4Path path = C4Path.path("c4://DevSpaces/DevSpaces API/Sign-In Component");

        collector.checkThat(path.name(), equalTo("Sign-In Component"));
        collector.checkThat(path.systemName(), equalTo("DevSpaces"));
        collector.checkThat(path.containerName(), equalTo(Optional.of("DevSpaces API")));
        collector.checkThat(path.componentName(), equalTo(Optional.of("Sign-In Component")));

        collector.checkThat(path.type(), equalTo(C4Type.component));
    }

    @Test(expected = IllegalArgumentException.class)
    public void missing_person() {
        C4Path.path("@");
    }

    @Test(expected = IllegalArgumentException.class)
    public void missing_system() {
        C4Path.path("c4://");
    }


    @Test(expected = IllegalStateException.class)
    public void accessing_system_path_on_non_system_throws_exception() {
        C4Path path = C4Path.path("@person");
        path.systemPath();
    }

    @Test(expected = IllegalStateException.class)
    public void accessing_person_path_on_non_person_throws_exception() {
        C4Path path = C4Path.path("c4://sys1");
        path.personPath();
    }

    @Test(expected = IllegalStateException.class)
    public void accessing_component_path_on_path_with_no_component_throws_exception() {
        C4Path path = C4Path.path("c4://sys1/container1");
        path.componentPath();
    }

    @Test
    public void system_path() {
        C4Path path = C4Path.path("c4://sys_1");
        collector.checkThat(path.systemPath(), equalTo(path));
    }

    @Test
    public void person_path() {
        C4Path path = C4Path.path("@person");
        collector.checkThat(path.personPath(), equalTo(path));
    }

    @Test
    public void should_be_able_to_extract_sub_paths_in_container_path() {
        C4Path path = C4Path.path("c4://sys1/container1");
        collector.checkThat(path.systemPath(), equalTo(C4Path.path("c4://sys1")));
        collector.checkThat(path.containerPath(), equalTo(path));
    }

    @Test
    public void should_be_able_to_extract_sub_paths_in_component_path() {
        C4Path path = C4Path.path("c4://sys1/container1/comp1");
        collector.checkThat(path.systemPath(), equalTo(C4Path.path("c4://sys1")));
        collector.checkThat(path.containerPath(), equalTo(C4Path.path("c4://sys1/container1")));
        collector.checkThat(path.componentPath(), equalTo(path));
    }

    @Test
    public void should_build_path_from_valid_paths() {
        collector.checkThat(C4Path.path("@Person"), notNullValue());
        collector.checkThat(C4Path.path("c4://system1"), notNullValue());
        collector.checkThat(C4Path.path("c4://system1/container1"), notNullValue());
        collector.checkThat(C4Path.path("c4://system1/container1/component1"), notNullValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_fail_to_build_path_if_prefix_is_invalid() {
        collector.checkThat(C4Path.path("{@Person"), notNullValue());
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
}
