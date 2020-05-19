package net.trilogy.arch.e2e.architectureUpdate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.trilogy.arch.Application;
import net.trilogy.arch.adapter.ArchitectureUpdateObjectMapper;
import net.trilogy.arch.adapter.FilesFacade;
import net.trilogy.arch.adapter.Jira.JiraApiFactory;
import net.trilogy.arch.adapter.in.google.GoogleDocsApiInterface;
import net.trilogy.arch.adapter.in.google.GoogleDocsAuthorizedApiFactory;
import net.trilogy.arch.domain.architectureUpdate.ArchitectureUpdate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.mockito.ArgumentMatchers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static net.trilogy.arch.TestHelper.execute;
import static net.trilogy.arch.commands.architectureUpdate.AuCommand.ARCHITECTURE_UPDATES_ROOT_FOLDER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuNewCommandTest {
    @Rule
    public final ErrorCollector collector = new ErrorCollector();

    private GoogleDocsApiInterface googleDocsApiMock;
    private Application app;
    private File rootDir;

    @Before
    public void setUp() throws Exception {
        rootDir = getTempDirectory().toFile();
        googleDocsApiMock = mock(GoogleDocsApiInterface.class);
        final var googleDocsApiFactoryMock = mock(GoogleDocsAuthorizedApiFactory.class);
        when(googleDocsApiFactoryMock.getAuthorizedDocsApi(rootDir)).thenReturn(googleDocsApiMock);
        var filesFacade = new FilesFacade();
        app = new Application(googleDocsApiFactoryMock, mock(JiraApiFactory.class), filesFacade);
    }

    @Test
    public void shouldExitWithHappyStatusWithoutP1_short() throws Exception {
        execute("au", "init", "-c c", "-p p", "-s s", str(rootDir.toPath()));
        collector.checkThat(
                execute("au", "new", "au-name", str(rootDir.toPath())),
                is(equalTo(0))
        );
    }

    @Test
    public void shouldExitWithHappyStatusWithoutP1_long() throws Exception {
        execute("au", "init", "-c c", "-p p", "-s s", str(rootDir.toPath()));
        collector.checkThat(
                execute("architecture-update", "new", "au-name", str(rootDir.toPath())),
                is(equalTo(0))
        );
    }

    @Test
    public void shouldExitWithHappyStatusWithP1_short() throws Exception {
        mockGoogleDocsApi();
        initializeAuDirectory(rootDir.toPath());
        collector.checkThat(
                execute(app, "au new au-name -p url " + str(rootDir.toPath())),
                is(equalTo(0))
        );
    }

    @Test
    public void shouldExitWithHappyStatusWithP1_long() throws Exception {
        mockGoogleDocsApi();
        initializeAuDirectory(rootDir.toPath());
        collector.checkThat(
                execute(app, "architecture-update new au-name --p1-url url " + str(rootDir.toPath())),
                is(equalTo(0))
        );
    }

    @Test
    public void shouldFailIfNotInitialized() throws Exception {
        collector.checkThat(
                ARCHITECTURE_UPDATES_ROOT_FOLDER + " folder does not exist. (Precondition check)",
                Files.exists(rootDir.toPath().resolve(ARCHITECTURE_UPDATES_ROOT_FOLDER)),
                is(false)
        );

        collector.checkThat(
                execute("au", "new", "au-name", str(rootDir.toPath())),
                not(equalTo(0))
        );
    }

    @Test
    public void shouldCreateFileWithoutP1() throws Exception {
        execute("init", str(rootDir.toPath()), "-i i", "-k k", "-s s");
        Path auDir = initializeAuDirectory(rootDir.toPath());
        Path auFile = auDir.resolve("au-name.yml");
        collector.checkThat(
                "AU does not already exist. (Precondition check)",
                Files.exists(auFile),
                is(false)
        );

        Integer exitCode = execute("au", "new", "au-name", str(rootDir.toPath()));

        collector.checkThat(exitCode, is(equalTo(0)));
        collector.checkThat(Files.exists(auFile), is(true));
        collector.checkThat(
                Files.readString(auFile.toAbsolutePath()),
                equalTo(
                    new ArchitectureUpdateObjectMapper().writeValueAsString(
                        ArchitectureUpdate.builderPreFilledWithBlanks().name("au-name").build()
                    )
                )
        );
    }

    @Test
    public void shouldCreateFileWithP1() throws Exception {
        // GIVEN
        mockGoogleDocsApi();
        execute("init", str(rootDir.toPath()), "-i i", "-k k", "-s s");
        Path auDir = initializeAuDirectory(rootDir.toPath());
        Path auFile = auDir.resolve("au-name.yml");
        collector.checkThat(
                "AU does not already exist. (Precondition check)",
                Files.exists(auFile),
                is(false)
        );

        // WHEN
        Integer exitCode = execute(app, "au new au-name -p url " + str(rootDir.toPath()));

        // THEN
        collector.checkThat(exitCode, is(equalTo(0)));
        collector.checkThat(Files.exists(auFile), is(true));
        collector.checkThat(
                Files.readString(auFile.toAbsolutePath()),
                containsString("ABCD-1231")
        );
    }

    @Test
    public void shouldNotCreateFileIfAlreadyExists() throws Exception {
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

    @Test
    public void shouldFailIfCannotWriteFile() throws Exception {
        Path rootDir = getTempDirectory();
        execute("au", "init", "-c c", "-p p", "-s s", str(rootDir));

        String auName = "au-name";

        var mockedFilesFacade = mock(FilesFacade.class);

        when(mockedFilesFacade.writeString(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenThrow(new IOException("No disk space!"));

        Application app = new Application(new GoogleDocsAuthorizedApiFactory(), mock(JiraApiFactory.class), mockedFilesFacade);
        final String command = "au new " + auName + " " + str(rootDir);

        assertThat(execute(app, command), not(equalTo(0)));
    }

    private void mockGoogleDocsApi() throws IOException {
        String rawFileContents = Files.readString(Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("Json/SampleP1-1.json")).getPath()));
        JsonNode jsonFileContents = new ObjectMapper().readValue(rawFileContents, JsonNode.class);
        when(googleDocsApiMock.fetch("url")).thenReturn(new GoogleDocsApiInterface.Response(jsonFileContents, null));
    }

    private Path initializeAuDirectory(Path rootDir) throws Exception {
        execute("au", "init", "-c c", "-p p", "-s s", str(rootDir));
        return rootDir.resolve(ARCHITECTURE_UPDATES_ROOT_FOLDER);
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
