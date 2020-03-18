package net.trilogy.arch.adapter;

import net.trilogy.arch.adapter.in.WorkspaceReader;
import net.trilogy.arch.domain.ArchitectureDataStructure;
import net.trilogy.arch.domain.c4.*;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import java.io.File;
import java.net.URL;

import static net.trilogy.arch.domain.c4.C4Action.USES;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class WorkspaceReaderTest {

    @Test
    public void shouldReadComponent() throws Exception {
        ArchitectureDataStructure data = readFromTestJson();

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
        ArchitectureDataStructure dataStructure = readFromTestJson();

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

    private ArchitectureDataStructure readFromTestJson() throws Exception {
        // TODO FUTURE: Probably a good idea to break out the giant .json into individual, small jsons per test with only what's needed.
        URL resource = getClass().getResource("/structurizr/Think3-Sococo.c4model.json");
        return new WorkspaceReader().load(new File(resource.getPath()));
    }
}
