package net.trilogy.arch.e2e;

import static net.trilogy.arch.TestHelper.execute;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import net.trilogy.arch.TestHelper;

public class ListComponentsCommandE2ETest {
    @Rule
    public final ErrorCollector collector = new ErrorCollector();

    private File rootDir;

    final PrintStream originalOut = System.out;
    final PrintStream originalErr = System.err;
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    final ByteArrayOutputStream err = new ByteArrayOutputStream();

    @Before
    public void setUp() {
        out.reset();
        err.reset();
        System.setOut(new PrintStream(out));
        System.setErr(new PrintStream(err));
        rootDir = new File(getClass().getResource(TestHelper.ROOT_PATH_TO_TEST_VALIDATION).getPath());
    }

    private void initFileForTest(String fileName) throws IOException {
        Files.copy(rootDir.toPath().resolve(fileName), rootDir.toPath().resolve("product-architecture.yml"));
    }

    @After
    public void tearDown() throws IOException {
        System.setOut(originalOut);
        System.setErr(originalErr);

        Files.deleteIfExists(rootDir.toPath().resolve("product-architecture.yml"));
    }

    @Test
    public void shouldOutputComponentsList() throws IOException {
        initFileForTest("allValidSchema.yml");
    }

    @Test
    public void shouldHandleEmptyArchitecture() throws IOException {
        initFileForTest("missingViewContexts.yml");
    }

    @Test
    public void shouldFailIfArchitectureNotFound() {
    }

    @Test
    public void shouldFailIfArchitectureInvalid() throws IOException {
        initFileForTest("missingModelProperties.yml");
    }

}
