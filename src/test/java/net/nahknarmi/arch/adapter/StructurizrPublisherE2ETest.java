package net.nahknarmi.arch.adapter;

import com.structurizr.api.StructurizrClientException;
import net.nahknarmi.arch.StructurizrPublisher;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static net.nahknarmi.arch.TestHelper.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class StructurizrPublisherE2ETest {

    @Test
    public void should_publish_architecture_data_structure_changes_to_structurizr() throws IOException, StructurizrClientException {
        //given
        File documentationRoot =
                new File(StructurizrPublisher.class.getResource(TEST_PRODUCT_DOCUMENTATION_ROOT_PATH).getPath());

        //when
        new StructurizrPublisher(documentationRoot).publish(TEST_WORKSPACE_ID, PRODUCT_NAME);

        //then
        StructurizrAdapter adapter = new StructurizrAdapter(TEST_WORKSPACE_ID);
        assertThat(adapter.load().getDocumentation().getSections().size(), equalTo(2));
        assertThat(adapter.load().getDocumentation().getDecisions().size(), equalTo(2));
    }
}
