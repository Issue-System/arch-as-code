package net.trilogy.arch.e2e.architectureUpdate;

import net.trilogy.arch.Application;
import net.trilogy.arch.TestHelper;
import net.trilogy.arch.adapter.FilesFacade;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuAnnotateCommandTest {
    @Rule
    public final ErrorCollector collector = new ErrorCollector();

    Path rootPath;
    Path changedAuPath;
    Path originalAuPath;

    final PrintStream originalOut = System.out;
    final PrintStream originalErr = System.err;
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    final ByteArrayOutputStream err = new ByteArrayOutputStream();

    @Before
    public void setUp() throws Exception {
        rootPath = TestHelper.getPath(getClass(), TestHelper.ROOT_PATH_TO_TEST_AU_ANNOTATE);
        originalAuPath = rootPath.resolve("architecture-updates/valid-with-components.yml");
        changedAuPath = rootPath.resolve("architecture-updates/valid-with-components-clone.yml");

        Files.copy(rootPath.resolve("architecture-updates/valid-with-components.yml"), changedAuPath);

        out.reset();
        err.reset();
        System.setOut(new PrintStream(out));
        System.setErr(new PrintStream(err));
    }

    @After
    public void tearDown() throws Exception {
        Files.deleteIfExists(changedAuPath);

        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    public void shouldAnnotate() throws Exception {
        // WHEN
        int status = TestHelper.execute(new Application(null, null, new FilesFacade()),
                "au annotate " + str(changedAuPath) + " " + str(rootPath));

        var actual = Files.readString(changedAuPath);

        // THEN
        var expected = Files.readString(originalAuPath).replaceFirst("'Component-31':",
                "'Component-31':  # c4://Internet Banking System/API Application/Reset Password Controller")
                .replaceFirst("Component-30:",
                        "Component-30:  # c4://Internet Banking System/API Application/Accounts Summary Controller")
                .replaceFirst("\"Component-34\":",
                        "\"Component-34\":  # c4://Internet Banking System/API Application/E-mail Component");

        collector.checkThat(status, equalTo(0));
        collector.checkThat(actual, equalTo(expected));
    }

    @Test
    public void shouldNotifyUserWhenAUFailsToLoad() throws Exception {
        // GIVEN
        final FilesFacade mockedFilesFacade = spy(FilesFacade.class);
        when(mockedFilesFacade.readString(changedAuPath)).thenThrow(new IOException("error-message", new RuntimeException("Boom!")));

        // WHEN
        int status = TestHelper.execute(new Application(null, null, mockedFilesFacade),
                "au annotate " + str(changedAuPath) + " " + str(rootPath));

        // THEN
        collector.checkThat(out.toString(), equalTo(""));
        collector.checkThat(err.toString(), equalTo("Unable to load Architecture Update.\nError: java.io.IOException: error-message\nCause: java.lang.RuntimeException: Boom!\n"));
        collector.checkThat(status, equalTo(2));
    }

    @Test
    public void shouldNotifyUserWhenArchitectureDatastructureFailsToLoad() {
        // TODO: Replace use files facade in architecture datastructure .load()
        // WHEN
        int status = TestHelper.execute(new Application(null, null, new FilesFacade()),
                "au annotate " + str(changedAuPath) + " " + "missing-file-path");

        // THEN
        collector.checkThat(out.toString(), equalTo(""));
        collector.checkThat(err.toString(), containsString("Unable to load Architecture data-structure.yml."));
        collector.checkThat(status, equalTo(2));
    }

    @Test
    public void shouldNotifuUserWhenAnnotationFailsToWrite() throws Exception {
        // GIVEN
        final FilesFacade mockedFilesFacade = mock(FilesFacade.class);
        when(mockedFilesFacade.readString(any())).thenCallRealMethod();
        when(mockedFilesFacade.writeString(any(), any())).thenThrow(new IOException("Ran out of bytes!"));

        // WHEN
        int status = TestHelper.execute(new Application(null, null, mockedFilesFacade),
                "au annotate " + str(changedAuPath) + " " + str(rootPath));

        // THEN
        collector.checkThat(out.toString(), equalTo(""));
        collector.checkThat(err.toString(), equalTo("Unable to write annotations to Architecture Update.\nError: java.io.IOException: Ran out of bytes!\nCause: null\n"));
        collector.checkThat(status, equalTo(2));

    }

    // TODO [TESTING] [ENHANCEMENT] : What happens if component reference is invalid?
    // TODO [TESTING] [ENHANCEMENT] : What happens if there are no components?

    private String str(Path path) {
        return path.toAbsolutePath().toString();
    }
}
