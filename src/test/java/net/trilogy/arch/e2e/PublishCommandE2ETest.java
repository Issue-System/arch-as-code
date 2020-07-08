package net.trilogy.arch.e2e;

import net.trilogy.arch.TestHelper;
import org.junit.Test;

import java.io.File;

import static net.trilogy.arch.TestHelper.execute;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

public class PublishCommandE2ETest {

    @Test
    public void publish() {
        File documentationRoot = new File(getClass().getResource(TestHelper.ROOT_PATH_TO_TEST_PRODUCT_DOCUMENTATION).getPath());

        Integer statusCode = execute("publish", documentationRoot.getAbsolutePath());

        // TODO [TESTING]: Ensure publish called
        assertThat(statusCode, equalTo(0));
    }

    @Test
    public void publish_invalid_manifest() {
        File documentationRoot = new File(getClass().getResource(TestHelper.ROOT_PATH_TO_TEST_VALIDATION).getPath());

        Integer statusCode = execute("publish", documentationRoot.getAbsolutePath(), "missingMetadata.yml");

        // TODO [TESTING]: Ensure publish NOT called

        // TODO [TESTING]: Ensure validation output displayed
        assertThat(statusCode, not(equalTo(0)));
    }
}
