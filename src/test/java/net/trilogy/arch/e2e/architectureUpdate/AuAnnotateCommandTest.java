package net.trilogy.arch.e2e.architectureUpdate;

import net.trilogy.arch.Application;
import net.trilogy.arch.TestHelper;
import net.trilogy.arch.adapter.git.GitInterface;
import net.trilogy.arch.facade.FilesFacade;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AuAnnotateCommandTest {
    @Rule
    public final ErrorCollector collector = new ErrorCollector();

    Path rootPath;
    Path originalAuWithComponentsPath;
    Path changedAuWithComponentsPath;
    Path originalAuWithoutComponentsPath;
    Path changedAuWithoutComponentsPath;

    final PrintStream originalOut = System.out;
    final PrintStream originalErr = System.err;
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    final ByteArrayOutputStream err = new ByteArrayOutputStream();

    @Before
    public void setUp() throws Exception {
        rootPath = TestHelper.getPath(getClass(), TestHelper.ROOT_PATH_TO_TEST_AU_ANNOTATE);
        originalAuWithComponentsPath = rootPath.resolve("architecture-updates/valid-with-components.yml");
        originalAuWithoutComponentsPath = rootPath.resolve("architecture-updates/valid-without-components.yml");

        changedAuWithComponentsPath = rootPath.resolve("architecture-updates/valid-with-components-clone.yml");
        changedAuWithoutComponentsPath = rootPath.resolve("architecture-updates/valid-without-components-clone.yml");

        Files.copy(originalAuWithComponentsPath, changedAuWithComponentsPath);
        Files.copy(originalAuWithoutComponentsPath, changedAuWithoutComponentsPath);

        out.reset();
        err.reset();
        System.setOut(new PrintStream(out));
        System.setErr(new PrintStream(err));
    }

    @After
    public void tearDown() throws Exception {
        Files.deleteIfExists(changedAuWithComponentsPath);
        Files.deleteIfExists(changedAuWithoutComponentsPath);

        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    public void shouldAnnotate() throws Exception {
        // WHEN
        int status = TestHelper.execute(new Application(null, null, new FilesFacade(), new GitInterface()),
                "au annotate " + str(changedAuWithComponentsPath) + " " + str(rootPath));

        var actual = Files.readString(changedAuWithComponentsPath);

        // THEN
        var expected = Files.readString(originalAuWithComponentsPath)
                .replaceFirst("'Component-31':",
                        "'Component-31':  # c4://Internet Banking System/API Application/Reset Password Controller")
                .replaceFirst("Component-30:",
                        "Component-30:  # c4://Internet Banking System/API Application/Accounts Summary Controller")
                .replaceFirst("\"Component-34\":",
                        "\"Component-34\":  # c4://Internet Banking System/API Application/E-mail Component");

        collector.checkThat(out.toString(), equalTo("AU has been annotated with component paths.\n"));
        collector.checkThat(status, equalTo(0));
        collector.checkThat(actual, equalTo(expected));
    }

    @Test
    public void shouldRefreshAnnotations() throws Exception {
        // GIVEN
        TestHelper.execute(new Application(null, null, new FilesFacade(), new GitInterface()),
                "au annotate " + str(changedAuWithComponentsPath) + " " + str(rootPath));

        Files.writeString(changedAuWithComponentsPath, Files.readString(changedAuWithComponentsPath).replace("Component-31", "Component-29"));

        // WHEN
        int status = TestHelper.execute(new Application(null, null, new FilesFacade(), new GitInterface()),
                "au annotate " + str(changedAuWithComponentsPath) + " " + str(rootPath));

        var actual = Files.readString(changedAuWithComponentsPath);

        // THEN
        var expected = Files.readString(originalAuWithComponentsPath)
                .replaceFirst("'Component-31':",
                        "'Component-29':  # c4://Internet Banking System/API Application/Sign In Controller")
                .replaceFirst("Component-30:",
                        "Component-30:  # c4://Internet Banking System/API Application/Accounts Summary Controller")
                .replaceFirst("\"Component-34\":",
                        "\"Component-34\":  # c4://Internet Banking System/API Application/E-mail Component");

        collector.checkThat(status, equalTo(0));
        collector.checkThat(actual, equalTo(expected));
    }

    @Test
    public void shouldHandleNoComponents() throws Exception {
        // WHEN
        int status = TestHelper.execute(new Application(null, null, new FilesFacade(), new GitInterface()),
                "au annotate " + str(changedAuWithoutComponentsPath) + " " + str(rootPath));

        var actual = Files.readString(changedAuWithoutComponentsPath);

        // THEN
        var expected = Files.readString(originalAuWithoutComponentsPath);

        collector.checkThat(out.toString(), equalTo("AU has been annotated with component paths.\n"));
        collector.checkThat(actual, equalTo(expected));
        collector.checkThat(status, equalTo(0));
    }

    @Test
    public void shouldIgnoreInvalidComponents() throws Exception {
        // GIVEN
        Files.writeString(changedAuWithComponentsPath, Files.readString(changedAuWithComponentsPath).replace("Component-31", "Component-404"));

        // WHEN
        int status = TestHelper.execute(new Application(null, null, new FilesFacade(), new GitInterface()),
                "au annotate " + str(changedAuWithComponentsPath) + " " + str(rootPath));

        var actual = Files.readString(changedAuWithComponentsPath);

        // THEN
        var expected = Files.readString(originalAuWithComponentsPath)
                .replaceFirst("'Component-31':", "'Component-404':")
                .replaceFirst("Component-30:",
                        "Component-30:  # c4://Internet Banking System/API Application/Accounts Summary Controller")
                .replaceFirst("\"Component-34\":",
                        "\"Component-34\":  # c4://Internet Banking System/API Application/E-mail Component");

        collector.checkThat(out.toString(), equalTo("AU has been annotated with component paths.\n"));
        collector.checkThat(actual, equalTo(expected));
        collector.checkThat(status, equalTo(0));
    }

    @Test
    public void shouldNotifyUserWhenAUFailsToLoad() throws Exception {
        // GIVEN
        final FilesFacade mockedFilesFacade = spy(FilesFacade.class);
        when(mockedFilesFacade.readString(changedAuWithComponentsPath)).thenThrow(new IOException("error-message", new RuntimeException("Boom!")));

        // WHEN
        int status = TestHelper.execute(new Application(null, null, mockedFilesFacade, new GitInterface()),
                "au annotate " + str(changedAuWithComponentsPath) + " " + str(rootPath));

        // THEN
        collector.checkThat(out.toString(), equalTo(""));
        collector.checkThat(
                err.toString(),
                equalTo("Unable to load Architecture Update.\nError: java.io.IOException: error-message\nCause: java.lang.RuntimeException: Boom!\n"));
        collector.checkThat(status, equalTo(2));
    }

    @Test
    public void shouldNotifyUserWhenArchitectureDatastructureFailsToLoad() throws Exception {
        // GIVEN
        final FilesFacade spyedFilesFacade = spy(new FilesFacade());
        final Path a = rootPath.resolve("product-architecture.yml");
        doThrow(new IOException("error-message", new RuntimeException("Boom!"))).when(spyedFilesFacade).readString(eq(a));

        // WHEN
        int status = TestHelper.execute(new Application(null, null, spyedFilesFacade, new GitInterface()),
                "au annotate " + str(changedAuWithComponentsPath) + " " + str(rootPath));

        // THEN
        collector.checkThat(out.toString(), equalTo(""));
        collector.checkThat(
                err.toString(),
                equalTo("Unable to load Architecture product-architecture.yml.\nError: java.io.IOException: error-message\nCause: java.lang.RuntimeException: Boom!\n"));
        collector.checkThat(status, equalTo(2));
    }

    @Test
    public void shouldNotifuUserWhenAnnotationFailsToWrite() throws Exception {
        // GIVEN
        final FilesFacade mockedFilesFacade = mock(FilesFacade.class);
        when(mockedFilesFacade.readString(any())).thenCallRealMethod();
        when(mockedFilesFacade.writeString(any(), any())).thenThrow(new IOException("Ran out of bytes!"));

        // WHEN
        int status = TestHelper.execute(new Application(null, null, mockedFilesFacade, new GitInterface()),
                "au annotate " + str(changedAuWithComponentsPath) + " " + str(rootPath));

        // THEN
        collector.checkThat(out.toString(), equalTo(""));
        collector.checkThat(
                err.toString(),
                equalTo("Unable to write annotations to Architecture Update.\nError: java.io.IOException: Ran out of bytes!\nCause: null\n"));
        collector.checkThat(status, equalTo(2));

    }

    private String str(Path path) {
        return path.toAbsolutePath().toString();
    }
}
