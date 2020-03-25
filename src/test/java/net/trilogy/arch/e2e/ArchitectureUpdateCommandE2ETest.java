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
    public void initShouldFailIfDirectoryAlreadyExists() throws IOException {
        Path rootDir = getTempDirectory();
        execute("au", "init", str(rootDir));

        assertThat(
                execute("au", "init", str(rootDir)),
                not(equalTo(0))
        );
        // TODO: check that nothing was overridden
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

    @Test
    public void newShouldExitWithHappyStatus() throws IOException {
        Path dir = getTempDirectory();
        execute("au", "init", str(dir));
        assertThat(
                execute("au", "new", "au-name", str(dir)),
                is(equalTo(0))
        );

        dir = getTempDirectory();
        execute("au", "init", str(dir));
        assertThat(
                execute("architecture-update", "new", "au-name", str(dir)),
                is(equalTo(0))
        );
    }

    @Test
    public void newShouldFailIfNotInitialized() throws IOException {
        Path tempDirPath = getTempDirectory();
        assertThat(
                ARCHITECTURE_UPDATES_ROOT_FOLDER + " folder does not exist. (Precondition check)",
                Files.exists(tempDirPath.resolve(ARCHITECTURE_UPDATES_ROOT_FOLDER)),
                is(false)
        );

        assertThat(
                execute("au", "new", "au-name", str(tempDirPath)),
                not(equalTo(0))
        );
    }

    @Test
    public void newShouldCreateFile() throws IOException {
        // GIVEN an initialized root dir
        Path rootDir = getTempDirectory();
        execute("init", str(rootDir), "-i i", "-k k", "-s s");

        // GIVEN an initialized au dir
        execute("au", "init", str(rootDir));
        Path auDir = rootDir.resolve(ARCHITECTURE_UPDATES_ROOT_FOLDER);

        // GIVEN that the au does not exist
        Path auFile = auDir.resolve("au-name");
        assertThat(
                "AU does not already exist. (Precondition check)",
                Files.exists(auFile),
                is(false)
        );

        // WHEN the new command is run
        Integer exitCode = execute("au", "new", "au-name", str(rootDir));

        // THEN the au should exist
        assertThat(exitCode, is(equalTo(0)));
        assertThat(Files.exists(auFile), is(true));

        // TODO: check that file content matches output of AuWriter
    }

    @Test
    public void newShouldNotCreateFileIfAlreadyExists() throws IOException {
        Path rootDir = getTempDirectory();
        execute("au", "init", str(rootDir));

        String name = "au-name";
        execute("au", "new", name, str(rootDir));
        assertThat(
                execute("au", "new", name, str(rootDir)),
                not(equalTo(0))
        );
        // TODO: check that file was not overridden
    }

    private String str(Path tempDirPath) {
        return tempDirPath.toAbsolutePath().toString();
    }

    private Path getTempDirectory() throws IOException {
        return Files.createTempDirectory("arch-as-code_architecture-update_command_tests");
    }
}
