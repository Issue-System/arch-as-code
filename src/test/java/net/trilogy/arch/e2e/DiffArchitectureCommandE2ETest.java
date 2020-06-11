package net.trilogy.arch.e2e;

import net.trilogy.arch.Application;
import net.trilogy.arch.TestHelper;
import net.trilogy.arch.adapter.architectureYaml.ArchitectureDataStructureObjectMapper;
import net.trilogy.arch.adapter.git.GitInterface;
import net.trilogy.arch.adapter.google.GoogleDocsAuthorizedApiFactory;
import net.trilogy.arch.adapter.jira.JiraApiFactory;
import net.trilogy.arch.domain.ArchitectureDataStructure;
import net.trilogy.arch.facade.FilesFacade;

import org.apache.commons.io.FileUtils;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;

import static net.trilogy.arch.TestHelper.execute;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DiffArchitectureCommandE2ETest {
    @Rule
    public final ErrorCollector collector = new ErrorCollector();

    final PrintStream originalOut = System.out;
    final PrintStream originalErr = System.err;
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    final ByteArrayOutputStream err = new ByteArrayOutputStream();

    private Application app;
    private File rootDir;
    private GitInterface mockedGitInterface;

    @Before
    public void setUp() throws Exception {
        out.reset();
        err.reset();
        System.setOut(new PrintStream(out));
        System.setErr(new PrintStream(err));

        rootDir = new File(getClass().getResource(TestHelper.ROOT_PATH_TO_TEST_DIFF_COMMAND).getPath());

        mockedGitInterface = mock(GitInterface.class);
        app = new Application(new GoogleDocsAuthorizedApiFactory(), new JiraApiFactory(), new FilesFacade(), mockedGitInterface);
    }

    @After
    public void tearDown() throws Exception {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    public void shouldFailIfCannotCreateDirectory() throws Exception {
        final String architectureAsString = new FilesFacade().readString(rootDir.toPath().resolve("product-architecture.yml"))
                .replaceAll("id: \"16\"", "id: \"116\"");
        final ArchitectureDataStructure dataStructure = new ArchitectureDataStructureObjectMapper().readValue(architectureAsString);
        when(mockedGitInterface.load("master", rootDir.toPath().resolve("product-architecture.yml"))).thenReturn(dataStructure);

        final var outputDir = Files.createTempDirectory("aac").toFile();

        final int status = execute(app, "diff -b master " + rootDir.getAbsolutePath() + " -o " + outputDir.getAbsolutePath());
        FileUtils.forceDelete(outputDir);

        collector.checkThat(status, not(equalTo(0)));
        collector.checkThat(err.toString(), containsString("Unable to create output directory"));
        collector.checkThat(out.toString(), equalTo(""));
    }

    @Test
    public void shouldCreateArchitectureDiffSvg() throws Exception {
        // GIVEN
        final String architectureAsString = new FilesFacade().readString(rootDir.toPath().resolve("product-architecture.yml"))
                .replaceAll("id: \"16\"", "id: \"116\"");
        final ArchitectureDataStructure dataStructure = new ArchitectureDataStructureObjectMapper().readValue(architectureAsString);
        when(mockedGitInterface.load("master", rootDir.toPath().resolve("product-architecture.yml"))).thenReturn(dataStructure);

        // WHEN
        final Integer status = execute(app, "diff -b master " + rootDir.getAbsolutePath() + " -o " + rootDir.toPath().resolve("outputDir").toAbsolutePath().toString());

        // THEN
        collector.checkThat(
                out.toString(),
                containsString("digraph diff {\n")
        );
        collector.checkThat(
                out.toString(),
                containsString("16")
        );
        collector.checkThat(
                out.toString(),
                containsString("116")
        );
        collector.checkThat(
                out.toString(),
                containsString("\n}")
        );
        collector.checkThat(
                err.toString(),
                equalTo("")
        );
        collector.checkThat(
                status,
                equalTo(0)
        );
    }
}
