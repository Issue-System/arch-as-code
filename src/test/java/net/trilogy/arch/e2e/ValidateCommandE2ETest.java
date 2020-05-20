package net.trilogy.arch.e2e;

import net.trilogy.arch.TestHelper;
import net.trilogy.arch.commands.ValidateCommand;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

public class ValidateCommandE2ETest {

    @Test
    public void shouldBeOkayWithMostlyEmptyImportedJson() throws Exception {
        String jsonPath = getClass().getResource(TestHelper.JSON_STRUCTURIZR_NO_SYSTEM).getPath();
        Path root = Files.createTempDirectory("aac").toAbsolutePath();

        TestHelper.execute("import", jsonPath, root.toString());

        int status = TestHelper.execute("validate", root.toString());
        assertThat(status, equalTo(0));
    }

    @Test
    public void validate() throws Exception {
        File documentationRoot = new File(getClass().getResource(TestHelper.ROOT_PATH_TO_TEST_PRODUCT_DOCUMENTATION).getPath());
        ValidateCommand validateCommand = new ValidateCommand(documentationRoot, "product-architecture.yml");

        Integer statusCode = validateCommand.call();

        assertThat(statusCode, equalTo(0));
    }

    @Test
    public void validate_missing_metadata() throws Exception {
        File file = new File(getClass().getResource(TestHelper.ROOT_PATH_TO_TEST_VALIDATION).getPath());
        ValidateCommand validateCommand = new ValidateCommand(file, "missingMetadata.yml");

        Integer statusCode = validateCommand.call();

        assertThat(statusCode, not(equalTo(0)));
    }
}
