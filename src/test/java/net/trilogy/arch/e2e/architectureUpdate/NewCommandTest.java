package net.trilogy.arch.e2e.architectureUpdate;

import net.trilogy.arch.adapter.ArchitectureUpdateObjectMapper;
import net.trilogy.arch.domain.ArchitectureUpdate;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static net.trilogy.arch.TestHelper.execute;
import static net.trilogy.arch.commands.architectureUpdate.ArchitectureUpdateCommand.ARCHITECTURE_UPDATES_ROOT_FOLDER;
import static org.hamcrest.Matchers.*;

public class NewCommandTest {
    @Rule
    public final ErrorCollector collector = new ErrorCollector();

    @Test
    public void shouldExitWithHappyStatusWithoutP1() throws IOException {
        Path dir = getTempDirectory();
        execute("au", "init", "-c c", "-p p", "-s s", str(dir));
        collector.checkThat(
                execute("au", "new", "au-name", str(dir)),
                is(equalTo(0))
        );

        dir = getTempDirectory();
        execute("au", "init", "-c c", "-p p", "-s s", str(dir));
        collector.checkThat(
                execute("architecture-update", "new", "au-name", str(dir)),
                is(equalTo(0))
        );
    }

    @Test()
    @Ignore
    public void shouldExitWithHappyStatusWithP1() throws IOException {
        Path dir = getTempDirectory();
        execute("au", "init", "-c c", "-p p", "-s s", str(dir));
        collector.checkThat(
                execute("au", "new", "au-name", "-p1 url", str(dir)),
                is(equalTo(0))
        );

        dir = getTempDirectory();
        execute("au", "init", "-c c", "-p p", "-s s", str(dir));
        collector.checkThat(
                execute("architecture-update", "new", "au-name", "-p1 url", str(dir)),
                is(equalTo(0))
        );
    }

    @Test
    public void shouldFailIfNotInitialized() throws IOException {
        Path tempDirPath = getTempDirectory();
        collector.checkThat(
                ARCHITECTURE_UPDATES_ROOT_FOLDER + " folder does not exist. (Precondition check)",
                Files.exists(tempDirPath.resolve(ARCHITECTURE_UPDATES_ROOT_FOLDER)),
                is(false)
        );

        collector.checkThat(
                execute("au", "new", "au-name", str(tempDirPath)),
                not(equalTo(0))
        );
    }

    @Test
    public void shouldCreateFile() throws IOException {
        // GIVEN an initialized root dir
        Path rootDir = getTempDirectory();
        execute("init", str(rootDir), "-i i", "-k k", "-s s");

        // GIVEN an initialized au dir
        execute("au", "init", "-c c", "-p p", "-s s", str(rootDir));
        Path auDir = rootDir.resolve(ARCHITECTURE_UPDATES_ROOT_FOLDER);

        // GIVEN that the au does not exist
        Path auFile = auDir.resolve("au-name.yml");
        collector.checkThat(
                "AU does not already exist. (Precondition check)",
                Files.exists(auFile),
                is(false)
        );

        // WHEN the new command is run
        Integer exitCode = execute("au", "new", "au-name", str(rootDir));

        // THEN the au should exist
        collector.checkThat(exitCode, is(equalTo(0)));
        collector.checkThat(Files.exists(auFile), is(true));

        // THEN the au contents should be a blank au
        collector.checkThat(
                Files.readString(auFile.toAbsolutePath()),
                equalTo(new ArchitectureUpdateObjectMapper().writeValueAsString(ArchitectureUpdate.blank()))
        );
    }

    @Test
    public void shouldNotCreateFileIfAlreadyExists() throws IOException {
        Path rootDir = getTempDirectory();
        execute("au", "init", "-c c", "-p p", "-s s", str(rootDir));

        String auName = "au-name";
        execute("au", "new", auName, str(rootDir));
        Files.writeString(auPathFrom(rootDir, auName), "EXISTING CONTENTS");

        collector.checkThat(
                "Precondition check: AU must contain our contents.",
                Files.readString(auPathFrom(rootDir, auName)),
                equalTo("EXISTING CONTENTS")
        );

        collector.checkThat(
                "Overwriting an AU must exit with failed status.",
                execute("au", "new", auName, str(rootDir)),
                not(equalTo(0))
        );

        collector.checkThat(
                "Must not overwrite an AU",
                Files.readString(auPathFrom(rootDir, auName)),
                equalTo("EXISTING CONTENTS")
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
