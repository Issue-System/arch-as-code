package net.trilogy.arch.e2e.architectureUpdate;

import net.trilogy.arch.Application;
import net.trilogy.arch.adapter.FilesFacade;
import net.trilogy.arch.adapter.in.google.GoogleDocsAuthorizedApiFactory;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.mockito.ArgumentMatchers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static net.trilogy.arch.adapter.in.google.GoogleDocsAuthorizedApiFactory.GOOGLE_DOCS_API_CLIENT_CREDENTIALS_FILE_NAME;
import static net.trilogy.arch.adapter.in.google.GoogleDocsAuthorizedApiFactory.GOOGLE_DOCS_API_CREDENTIALS_FOLDER_PATH;
import static net.trilogy.arch.TestHelper.execute;
import static net.trilogy.arch.commands.architectureUpdate.ArchitectureUpdateCommand.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuInitializeCommandTest {
    @Rule
    public final ErrorCollector collector = new ErrorCollector();
    private final String client_id = "id";
    private final String project_id = "proj";
    private final String secret = "secret";

    @Test
    public void shouldUseCorrectAuFolder() {
        collector.checkThat(ARCHITECTURE_UPDATES_ROOT_FOLDER, equalTo("architecture-updates"));
    }

    @Test
    public void rootCommandShouldPrintUsage() throws Exception {
        // TODO OPTIONAL: Move test to file that only has root command tests

        // TODO FUTURE: Assert that usage is shown AAC-75
        collector.checkThat(
                execute("au"),
                equalTo(0)
        );
    }

    @Test
    public void shouldExitWithHappyStatus() throws Exception {
        collector.checkThat(
                execute("au", "init", "-c c", "-p p", "-s s", str(getTempDirectory())),
                equalTo(0)
        );
        collector.checkThat(
                execute("architecture-update", "init", "-c c", "-p p", "-s s", str(getTempDirectory())),
                equalTo(0)
        );
        collector.checkThat(
                execute("au", "initialize", "-c c", "-p p", "-s s", str(getTempDirectory())),
                equalTo(0)
        );
        collector.checkThat(
                execute("architecture-update", "initialize", "-c c", "-p p", "-s s", str(getTempDirectory())),
                equalTo(0)
        );
    }

    @Test
    public void shouldCreateAuDirectory() throws Exception {
        Path tempDirPath = getTempDirectory();
        collector.checkThat(
                ARCHITECTURE_UPDATES_ROOT_FOLDER + " folder does not exist. (Precondition check)",
                Files.exists(tempDirPath.resolve(ARCHITECTURE_UPDATES_ROOT_FOLDER)),
                is(false)
        );

        Integer status = execute("au", "init", "-c c", "-p p", "-s s", str(tempDirPath));

        collector.checkThat(status, is(equalTo(0)));

        collector.checkThat(
                ARCHITECTURE_UPDATES_ROOT_FOLDER + " folder was created.",
                Files.isDirectory(tempDirPath.resolve(ARCHITECTURE_UPDATES_ROOT_FOLDER)),
                is(true)
        );
    }

    @Test
    public void shouldCreateGoogleCredentialsDirectory() throws Exception {
        Path tempDirPath = getTempDirectory();
        collector.checkThat(
                GOOGLE_DOCS_API_CREDENTIALS_FOLDER_PATH + " folder does not exist. (Precondition check)",
                Files.exists(tempDirPath.resolve(GOOGLE_DOCS_API_CREDENTIALS_FOLDER_PATH)),
                is(false)
        );

        Integer status = execute("au", "init", "-c c", "-p p", "-s s", str(tempDirPath));

        collector.checkThat(status, is(equalTo(0)));

        collector.checkThat(
                GOOGLE_DOCS_API_CREDENTIALS_FOLDER_PATH + " folder was created.",
                Files.isDirectory(tempDirPath.resolve(GOOGLE_DOCS_API_CREDENTIALS_FOLDER_PATH)),
                is(true)
        );
    }

    @Test
    public void shouldFailIfCreatingCredentialsFileFails() throws Exception {
        // Given
        Path tempDirPath = getTempDirectory();
        collector.checkThat(
                GOOGLE_DOCS_API_CREDENTIALS_FOLDER_PATH + " folder does not exist. (Precondition check)",
                Files.exists(tempDirPath.resolve(GOOGLE_DOCS_API_CREDENTIALS_FOLDER_PATH)),
                is(false)
        );
        var mockedFilesAdapter = mock(FilesFacade.class);
        when(
                mockedFilesAdapter.writeString(ArgumentMatchers.any(), ArgumentMatchers.any())
        ).thenThrow(
                new IOException("Something horrible has happened. Maybe we ran out of bytes.")
        );
        var app = new Application(new GoogleDocsAuthorizedApiFactory(), mockedFilesAdapter);

        // when
        Integer status = execute(app, "au init -c c -p p -s s " + str(tempDirPath));

        // then
        collector.checkThat(status, not(equalTo(0)));
    }

    @Test
    public void shouldFailIfAuDirectoryAlreadyExists() throws Exception {
        Path rootDir = getTempDirectory();
        execute("au", "init", "-c c", "-p p", "-s s", str(rootDir));
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
    public void shouldRequireDocumentRootParameter() throws Exception {
        collector.checkThat(
                execute("au", "init"),
                is(equalTo(2))
        );
    }

    @Test
    public void shouldCreateClientCredentialsFile() throws Exception {
        Path rootDir = getTempDirectory();
        execute("au", "init", str(rootDir), "-c " + client_id, "-p " + project_id, "-s " + secret);

        Path auCredFile = rootDir.resolve(GOOGLE_DOCS_API_CREDENTIALS_FOLDER_PATH).resolve(GOOGLE_DOCS_API_CLIENT_CREDENTIALS_FILE_NAME);
        String expected = "{\n" +
                "  \"installed\": {\n" +
                "    \"client_id\": \"" + client_id + "\",\n" +
                "    \"project_id\": \"" + project_id + "\",\n" +
                "    \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                "    \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                "    \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                "    \"client_secret\": \"" + secret + "\",\n" +
                "    \"redirect_uris\": [\n" +
                "      \"urn:ietf:wg:oauth:2.0:oob\",\n" +
                "      \"http://localhost\"\n" +
                "    ]\n" +
                "  }\n" +
                "}";

        collector.checkThat(
                Files.readString(auCredFile),
                equalTo(expected));
    }

    @Test
    public void shouldNotOverrideExistingCredentialsFolder() throws Exception {
        Path rootDir = getTempDirectory();
        execute("au", "init", "-c c", "-p p", "-s s", str(rootDir));
        final Path auClientCredentialsFile = rootDir.resolve(GOOGLE_DOCS_API_CREDENTIALS_FOLDER_PATH).resolve(GOOGLE_DOCS_API_CLIENT_CREDENTIALS_FILE_NAME);
        Files.writeString(auClientCredentialsFile.toAbsolutePath(), "EXISTING CONTENTS");

        collector.checkThat(
                "Precondition check: AU must contain our contents.",
                Files.readString(auClientCredentialsFile),
                equalTo("EXISTING CONTENTS")
        );

        Integer result = execute("au", "init", "-c c", "-p p", "-s s", str(rootDir));

        collector.checkThat(
                result,
                not(equalTo(0))
        );
        collector.checkThat(
                Files.readString(auClientCredentialsFile),
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
