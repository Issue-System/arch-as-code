package net.trilogy.arch.e2e.architectureUpdate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.trilogy.arch.Application;
import net.trilogy.arch.adapter.architectureUpdateYaml.ArchitectureUpdateObjectMapper;
import net.trilogy.arch.adapter.git.GitInterface;
import net.trilogy.arch.adapter.google.GoogleDocsApiInterface;
import net.trilogy.arch.adapter.google.GoogleDocsAuthorizedApiFactory;
import net.trilogy.arch.adapter.jira.JiraApiFactory;
import net.trilogy.arch.domain.architectureUpdate.ArchitectureUpdate;
import net.trilogy.arch.facade.FilesFacade;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.mockito.ArgumentMatchers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static net.trilogy.arch.TestHelper.execute;
import static net.trilogy.arch.commands.architectureUpdate.AuCommand.ARCHITECTURE_UPDATES_ROOT_FOLDER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class AuNewCommandTest {
    @Rule
    public final ErrorCollector collector = new ErrorCollector();

    private GoogleDocsApiInterface googleDocsApiMock;
    private FilesFacade filesFacadeSpy;
    private GitInterface gitInterfaceSpy;
    private Application app;
    private File rootDir;

    final PrintStream originalOut = System.out;
    final PrintStream originalErr = System.err;
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    final ByteArrayOutputStream err = new ByteArrayOutputStream();

    @Before
    public void setUp() throws Exception {
        out.reset();
        err.reset();
        System.setOut(new PrintStream(out));
        System.setErr(new PrintStream(err));

        rootDir = getTempRepositoryDirectory().toFile();
        googleDocsApiMock = mock(GoogleDocsApiInterface.class);
        final var googleDocsApiFactoryMock = mock(GoogleDocsAuthorizedApiFactory.class);
        when(googleDocsApiFactoryMock.getAuthorizedDocsApi(rootDir)).thenReturn(googleDocsApiMock);
        filesFacadeSpy = spy(new FilesFacade());
        gitInterfaceSpy = spy(new GitInterface());

        app = new Application(googleDocsApiFactoryMock, mock(JiraApiFactory.class), filesFacadeSpy, gitInterfaceSpy);
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.forceDelete(rootDir);
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    public void shouldFailGracefullyIfGitApiFails() throws Exception {
        execute("au", "init", "-c c", "-p p", "-s s", str(rootDir.toPath()));

        doThrow(new RuntimeException("Boom!")).when(gitInterfaceSpy).getBranch(any());

        int status = execute(app, "au new not-au-name " + str(rootDir.toPath()));

        collector.checkThat(status, not(equalTo(0)));
        collector.checkThat(out.toString(), equalTo(""));
        collector.checkThat(err.toString(), containsString(
                "ERROR: Unable to check git branch\nError thrown:"
        ));
    }

    @Test
    public void shouldFailIfBranchNameDoesNotMatch() throws Exception {
        execute("au", "init", "-c c", "-p p", "-s s", str(rootDir.toPath()));

        int status = execute("au", "new", "not-au-name", str(rootDir.toPath()));

        collector.checkThat(status, not(equalTo(0)));
        collector.checkThat(out.toString(), equalTo(""));
        collector.checkThat(err.toString(), containsString(
                "ERROR: AU must be created in git branch of same name.\n" +
                        "Current git branch: 'au-name'\n" +
                        "Au name: 'not-au-name'\n"
        ));
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
    public void shouldFailGracefullyIfGoogleApiUninitialized() throws Exception {
        int status = execute("au", "new", "au-name", str(rootDir.toPath()), "-p", "p1GoogleDocUrl");

        Path auFile = rootDir.toPath().resolve("architecture-updates/au-name.yml");
        String configPath = rootDir.toPath().resolve(".arch-as-code").toAbsolutePath().toString();

        collector.checkThat(status, not(equalTo(0)));
        collector.checkThat(Files.exists(auFile), is(false));
        collector.checkThat(err.toString(), containsString("Unable to initialize Google Docs API. Does configuration " + configPath + " exist?\n"));
    }

    @Test
    public void shouldFailGracefullyIfFailsToCreateDirectory() throws Exception {
        Path auFolder = rootDir.toPath().resolve(ARCHITECTURE_UPDATES_ROOT_FOLDER);
        collector.checkThat(
                ARCHITECTURE_UPDATES_ROOT_FOLDER + " folder does not exist. (Precondition check)",
                Files.exists(auFolder),
                is(false)
        );

        doThrow(new IOException("details", new RuntimeException("cause"))).when(filesFacadeSpy).createDirectory(eq(auFolder));

        int status = execute(app, "au new au-name " + str(rootDir.toPath()));

        Path auFile = rootDir.toPath().resolve("architecture-updates/au-name.yml");

        collector.checkThat(status, not(equalTo(0)));
        collector.checkThat(Files.exists(auFile), is(false));
        collector.checkThat(err.toString(), containsString("Unable to create architecture-updates directory."));
        collector.checkThat(err.toString(), containsString("details"));
        collector.checkThat(err.toString(), containsString("cause"));
    }

    @Test
    public void shouldHandleCreatingFolderIfDoesNotExist() throws Exception {
        collector.checkThat(
                ARCHITECTURE_UPDATES_ROOT_FOLDER + " folder does not exist. (Precondition check)",
                Files.exists(rootDir.toPath().resolve(ARCHITECTURE_UPDATES_ROOT_FOLDER)),
                is(false)
        );

        int status = execute("au", "new", "au-name", str(rootDir.toPath()));

        Path auFile = rootDir.toPath().resolve("architecture-updates/au-name.yml");

        collector.checkThat(status, equalTo(0));
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
        Path rootDir = getTempRepositoryDirectory();
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
        Path rootDir = getTempRepositoryDirectory();
        execute("au", "init", "-c c", "-p p", "-s s", str(rootDir));

        String auName = "au-name";

        var mockedFilesFacade = mock(FilesFacade.class);

        when(mockedFilesFacade.writeString(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenThrow(new IOException("No disk space!"));

        Application app = new Application(new GoogleDocsAuthorizedApiFactory(), mock(JiraApiFactory.class), mockedFilesFacade, new GitInterface());
        final String command = "au new " + auName + " " + str(rootDir);

        assertThat(execute(app, command), not(equalTo(0)));
        collector.checkThat(err.toString(), containsString("Unable to write AU file."));
        collector.checkThat(err.toString(), containsString("No disk space!"));
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

    private Path getTempRepositoryDirectory() throws Exception {
        var repoDir = Files.createTempDirectory("aac");
        var rootDir = Files.createDirectory(repoDir.resolve("root"));
        var git = Git.init().setDirectory(repoDir.toFile()).call();
        git.add().addFilepattern(".").call();
        git.commit().setMessage("First!").call();
        git.checkout().setCreateBranch(true).setName("au-name").call();
        return rootDir;
    }
}
