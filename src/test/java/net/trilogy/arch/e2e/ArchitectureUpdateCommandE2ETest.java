package net.trilogy.arch.e2e;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static net.trilogy.arch.TestHelper.execute;
import static net.trilogy.arch.commands.ArchitectureUpdateCommand.ARCHITECTURE_UPDATES_ROOT_FOLDER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ArchitectureUpdateCommandE2ETest {

    @Test
    public void shouldUseCorrectFolder() {
        assertThat(ARCHITECTURE_UPDATES_ROOT_FOLDER, is(equalTo("architecture-updates")));
    }

    @Test
    public void rootCommandShouldPrintUsage() {
        // TODO: assert that usage is shown
        assertThat(
                execute("au"),
                is(equalTo(0))
        );
    }

    @Test
    public void initShouldExitWithHappyStatus() throws IOException {
        assertThat(
                execute("au", "init", str(getTempDirectory())),
                is(equalTo(0))
        );
        assertThat(
                execute("architecture-update", "init", str(getTempDirectory())),
                is(equalTo(0))
        );
        assertThat(
                execute("au", "initialize", str(getTempDirectory())),
                is(equalTo(0))
        );
        assertThat(
                execute("architecture-update", "initialize", str(getTempDirectory())),
                is(equalTo(0))
        );
    }

    @Test
    public void initShouldCreateDirectory() throws IOException {
        Path tempDirPath = getTempDirectory();
        assertThat(
                ARCHITECTURE_UPDATES_ROOT_FOLDER + " folder does not exist. (Precondition check)",
                Files.exists(tempDirPath.resolve(ARCHITECTURE_UPDATES_ROOT_FOLDER)),
                is(false)
        );

        Integer status = execute("au", "init", str(tempDirPath));

        assertThat(status, is(equalTo(0)));

        assertThat(
                ARCHITECTURE_UPDATES_ROOT_FOLDER + " folder was created.",
                Files.isDirectory(tempDirPath.resolve(ARCHITECTURE_UPDATES_ROOT_FOLDER)),
                is(true)
        );
    }

    @Test
    public void initShouldReturnSadStatusWhenFailToCreateDirectory() {
        Integer status = execute("au", "init", "???");

        assertThat(status, not(equalTo(0)));
    }

    @Test
    public void initShouldRequireDocumentRootParameter() {
        assertThat(
                execute("au", "init"),
                is(equalTo(2))
        );
    }

    private String str(Path tempDirPath) {
        return tempDirPath.toAbsolutePath().toString();
    }

    private Path getTempDirectory() throws IOException {
        return Files.createTempDirectory("arch-as-code_architecture-update_command_tests");
    }
}
