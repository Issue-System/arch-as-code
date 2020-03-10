package net.trilogy.arch.e2e;

import net.trilogy.arch.TestHelper;
import net.trilogy.arch.commands.PublishCommand;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

public class PublishCommandE2ETest {

    @Test
    public void publish() throws Exception {
        File documentationRoot = new File(getClass().getResource(TestHelper.TEST_PRODUCT_DOCUMENTATION_ROOT_PATH).getPath());
        PublishCommand publishCommand = new PublishCommand(documentationRoot, "data-structure.yml");

        Integer statusCode = publishCommand.call();

        assertThat(statusCode, equalTo(0));
    }

    @Test
    public void publish_invalid_manifest() throws Exception {
        File documentationRoot = new File(getClass().getResource(TestHelper.TEST_VALIDATION_ROOT_PATH).getPath());
        PublishCommand publishCommand = new PublishCommand(documentationRoot, "missingMetadata.yml");

        Integer statusCode = publishCommand.call();

        assertThat(statusCode, not(equalTo(0)));
    }
}
