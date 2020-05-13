package net.trilogy.arch.e2e.architectureUpdate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import net.trilogy.arch.Application;
import net.trilogy.arch.TestHelper;
import net.trilogy.arch.adapter.FilesFacade;
import net.trilogy.arch.adapter.in.ArchitectureDataStructureReader;
import net.trilogy.arch.domain.architectureUpdate.ArchitectureUpdate;

import static org.hamcrest.Matchers.*;

public class AuAnnotateCommandTest {
    @Rule
    public final ErrorCollector collector = new ErrorCollector();

    Path rootPath;
    Path changedAuPath;
    Path originalAuPath;

    @Before
    public void setUp() throws Exception {
        rootPath = TestHelper.getPath(getClass(), TestHelper.ROOT_PATH_TO_TEST_AU_ANNOTATE);
        originalAuPath = rootPath.resolve("architecture-updates/valid-with-components.yml");
        changedAuPath = rootPath.resolve("architecture-updates/valid-with-components-clone.yml");

        Files.copy(rootPath.resolve("architecture-updates/valid-with-components.yml"),
                changedAuPath);
    }

    @After
    public void tearDown() throws Exception {
        Files.deleteIfExists(changedAuPath);
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

    // TODO [TESTING] [ENHANCEMENT] : What happens if unable to load architecture?
    // TODO [TESTING] [ENHANCEMENT] : What happens if unable to load au?
    // TODO [TESTING] [ENHANCEMENT] : What happens if component reference is invalid?
    // TODO [TESTING] [ENHANCEMENT] : What happens if writing IO fails?
    // TODO [TESTING] [ENHANCEMENT] : What happens if there are no components? 

    private String str(Path path) {
        return path.toAbsolutePath().toString();
    }
}
