package net.nahknarmi.arch.adapter;

import com.structurizr.Workspace;
import com.structurizr.api.StructurizrClientException;
import net.nahknarmi.arch.publish.ArchitectureDataStructurePublisher;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static net.nahknarmi.arch.TestHelper.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;

public class ArchitectureDataStructurePublishingE2ETest {

    @Test
    public void should_publish_architecture_data_structure_changes_to_structurizr() throws IOException, StructurizrClientException {
        //given
        File documentationRoot =
                new File(getClass().getResource(TEST_PRODUCT_DOCUMENTATION_ROOT_PATH).getPath());

        //when
        ArchitectureDataStructurePublisher.create(documentationRoot).publish(PRODUCT_NAME);

        //then
        StructurizrAdapter adapter = new StructurizrAdapter();
        Workspace workspace = adapter.load(TEST_WORKSPACE_ID);
        assertThat(workspace.getDocumentation().getSections(), hasSize(2));
        assertThat(workspace.getDocumentation().getDecisions(), hasSize(2));
        assertThat(workspace.getModel().getSoftwareSystems(), hasSize(4));
        assertThat(workspace.getModel().getPeople(), hasSize(3));
        assertEquals(getTotalContainerCount(workspace), 4);
        assertEquals(getTotalComponentCount(workspace), 4);
        assertThat(workspace.getModel().getRelationships(), hasSize(18));
    }

    private int getTotalComponentCount(Workspace workspace) {
        return workspace
                .getModel()
                .getSoftwareSystems()
                .stream()
                .map(s -> s.getContainers()
                        .stream()
                        .map(c -> c.getComponents().size())
                        .reduce(0, Integer::sum)
                )
                .reduce(0, Integer::sum);
    }

    private int getTotalContainerCount(Workspace workspace) {
        return workspace
                .getModel()
                .getSoftwareSystems()
                .stream()
                .map(s -> s.getContainers().size())
                .reduce(0, Integer::sum);
    }
}
