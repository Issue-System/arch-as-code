package net.nahknarmi.arch.adapter;

import com.structurizr.Workspace;
import com.structurizr.util.WorkspaceUtils;
import net.nahknarmi.arch.domain.ArchitectureDataStructure;
import org.junit.Test;

import java.io.File;
import java.net.URL;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;


public class WorkspaceConverterTest {

    @Test
    public void convert() throws Exception {
        URL resource = getClass().getResource("/structurizr/Think3-Sococo.c4model.json");
        Workspace workspace = WorkspaceUtils.loadWorkspaceFromJson(new File(resource.getPath()));

        ArchitectureDataStructure dataStructure =
                new WorkspaceConverter().convert(workspace);

        assertThat(dataStructure.getName(), is(equalTo("Sococo Import")));
        assertThat(dataStructure.getBusinessUnit(), is(equalTo("Think3")));

        assertThat(dataStructure, is(notNullValue()));
        assertThat(dataStructure.getModel(), is(notNullValue()));
        assertThat(dataStructure.getModel().getPeople(), hasSize(7));
        assertThat(dataStructure.getModel().getSystems(), hasSize(13));
        assertThat(dataStructure.getModel().getContainers(), hasSize(28));
        assertThat(dataStructure.getModel().getComponents(), hasSize(35));
        assertThat(dataStructure.getModel().allRelationships(), hasSize(160));

        assertThat(dataStructure.getViews().getComponentViews(), hasSize(8));
        assertThat(dataStructure.getViews().getContainerViews(), hasSize(9));
        assertThat(dataStructure.getViews().getSystemViews(), hasSize(6));
    }
}