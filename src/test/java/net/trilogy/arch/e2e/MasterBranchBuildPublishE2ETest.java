package net.trilogy.arch.e2e;

import net.trilogy.arch.commands.PublishCommand;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class MasterBranchBuildPublishE2ETest {

    @Test
    public void publish() throws Exception {
        File documentationRoot = new File("documentation/products/arch-as-code");
        PublishCommand publishCommand = new PublishCommand(documentationRoot, "product-architecture.yml");

        Integer statusCode = publishCommand.call();

        assertThat(statusCode, equalTo(0));
    }
}
