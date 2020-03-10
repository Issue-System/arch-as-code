package net.trilogy.arch.adapter;

import net.trilogy.arch.adapter.in.WorkspaceReader;
import net.trilogy.arch.domain.ArchitectureDataStructure;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import java.io.File;
import java.net.URL;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;


public class WorkspaceReaderTest {

    @Test
    public void convert() throws Exception {
        URL resource = getClass().getResource("/structurizr/Think3-Sococo.c4model.json");
        ArchitectureDataStructure dataStructure =
                new WorkspaceReader().load(new File(resource.getPath()));

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
}
