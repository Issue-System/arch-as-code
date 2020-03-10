package net.trilogy.arch.e2e;

import net.trilogy.arch.commands.ImportCommand;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ImportCommandE2ETest {

    @Test
    public void import_workspace() throws Exception {
        File workspacePath = new File(getClass().getResource("/structurizr/Think3-Sococo.c4model.json").getPath());
        ImportCommand importCommand = new ImportCommand(workspacePath);

        Integer statusCode = importCommand.call();

        assertThat(statusCode, equalTo(0));
    }
}
