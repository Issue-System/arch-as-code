package net.trilogy.arch.e2e.architectureUpdate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import net.trilogy.arch.TestHelper;
import net.trilogy.arch.domain.architectureUpdate.ArchitectureUpdate;

import static org.hamcrest.Matchers.*;

public class AuAnnotateCommandTest {
    @Rule
    public final ErrorCollector collector = new ErrorCollector();

    Path rootPath;
    Path auPath;

    @Before
    public void setUp() throws Exception {
        rootPath = TestHelper.getPath(getClass(), TestHelper.ROOT_PATH_TO_TEST_AU_ANNOTATE);
        auPath = rootPath.resolve("architecture-updates/valid-with-components-clone.yml");

        Files.copy(rootPath.resolve("architecture-updates/valid-with-components.yml"), auPath);
    }

    @After
    public void tearDown() throws Exception {
        Files.deleteIfExists(auPath);
    }

    @Test
    public void shouldAnnotate() throws Exception {
        int status = TestHelper.execute(
            "au",
            "annotate", 
            auPath.toAbsolutePath().toString(),
            rootPath.toAbsolutePath().toString()
        );

        collector.checkThat(status, equalTo(0));
    }
}
