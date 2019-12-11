package net.nahknarmi.arch.adapter;

import com.structurizr.api.StructurizrClientException;
import net.nahknarmi.arch.StructurizrPublisher;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static net.nahknarmi.arch.TestHelper.TEST_SPACES_MANIFEST_PATH;
import static net.nahknarmi.arch.TestHelper.TEST_WORKSPACE_ID;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class StructurizrPublisherE2ETest {

    @Test
    public void should_publish_architecture_data_structure_changes_to_structurizr() throws IOException, StructurizrClientException {
        //given
        InputStream manifest = StructurizrPublisher.class.getResourceAsStream(TEST_SPACES_MANIFEST_PATH);

        //when
        new StructurizrPublisher().publish(TEST_WORKSPACE_ID, manifest);

        //then
        StructurizrAdapter adapter = new StructurizrAdapter(TEST_WORKSPACE_ID);
        assertThat(adapter.load().getDocumentation().getSections().size(), equalTo(2));
        assertThat(adapter.load().getDocumentation().getDecisions().size(), equalTo(2));
    }
}
