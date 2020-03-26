package net.trilogy.arch.e2e.architectureUpdate;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static net.trilogy.arch.TestHelper.execute;
import static net.trilogy.arch.commands.architectureUpdate.ArchitectureUpdateCommand.ARCHITECTURE_UPDATES_ROOT_FOLDER;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class InitializeCommandTest {
    @Rule
    public final ErrorCollector collector = new ErrorCollector();

    @Test
    public void shouldUseCorrectFolder() {
        collector.checkThat(ARCHITECTURE_UPDATES_ROOT_FOLDER, equalTo("architecture-updates"));
    }

    @Test
    public void rootCommandShouldPrintUsage() {
        // TODO: assert that usage is shown
        collector.checkThat(
                execute("au"),
                equalTo(0)
        );
    }

    @Test
    public void shouldExitWithHappyStatus() throws IOException {
        collector.checkThat(
                execute("au", "init", str(getTempDirectory())),
                equalTo(0)
        );
        collector.checkThat(
                execute("architecture-update", "init", str(getTempDirectory())),
                equalTo(0)
        );
        collector.checkThat(
                execute("au", "initialize", str(getTempDirectory())),
                equalTo(0)
        );
        collector.checkThat(
                execute("architecture-update", "initialize", str(getTempDirectory())),
                equalTo(0)
        );
    }

    @Test
    public void shouldCreateDirectory() throws IOException {
        Path tempDirPath = getTempDirectory();
        collector.checkThat(
                ARCHITECTURE_UPDATES_ROOT_FOLDER + " folder does not exist. (Precondition check)",
                Files.exists(tempDirPath.resolve(ARCHITECTURE_UPDATES_ROOT_FOLDER)),
                is(false)
        );

        Integer status = execute("au", "init", str(tempDirPath));

        collector.checkThat(status, is(equalTo(0)));

        collector.checkThat(
                ARCHITECTURE_UPDATES_ROOT_FOLDER + " folder was created.",
                Files.isDirectory(tempDirPath.resolve(ARCHITECTURE_UPDATES_ROOT_FOLDER)),
                is(true)
        );
    }

    @Test
    public void shouldFailIfDirectoryAlreadyExists() throws IOException {
        Path rootDir = getTempDirectory();
        execute("au", "init", str(rootDir));
        Files.writeString(auPathFrom(rootDir, "name"), "EXISTING CONTENTS");
        collector.checkThat(
                "Precondition check: AU must contain our contents.",
                Files.readString(auPathFrom(rootDir, "name")),
                equalTo("EXISTING CONTENTS")
        );

        Integer result = execute("au", "init", str(rootDir));

        collector.checkThat(
                result,
                not(equalTo(0))
        );
        collector.checkThat(
                Files.readString(auPathFrom(rootDir, "name")),
                equalTo("EXISTING CONTENTS")
        );
    }

    @Test
    public void shouldReturnSadStatusWhenFailToCreateDirectory() {
        Integer status = execute("au", "init", "???");

        collector.checkThat(status, not(equalTo(0)));
    }

    @Test
    public void shouldRequireDocumentRootParameter() {
        collector.checkThat(
                execute("au", "init"),
                is(equalTo(2))
        );
    }

    private Path auPathFrom(Path rootPath, String auName) {
        return rootPath.resolve(ARCHITECTURE_UPDATES_ROOT_FOLDER).resolve(auName).toAbsolutePath();
    }

    private String str(Path tempDirPath) {
        return tempDirPath.toAbsolutePath().toString();
    }

    private Path getTempDirectory() throws IOException {
        return Files.createTempDirectory("arch-as-code_architecture-update_command_tests");
    }
}
