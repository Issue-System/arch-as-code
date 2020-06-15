package net.trilogy.arch.e2e;

import static net.trilogy.arch.TestHelper.execute;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import net.trilogy.arch.Application;
import net.trilogy.arch.TestHelper;
import net.trilogy.arch.adapter.architectureYaml.ArchitectureDataStructureObjectMapper;
import net.trilogy.arch.adapter.git.GitInterface;
import net.trilogy.arch.adapter.graphviz.GraphvizInterface;
import net.trilogy.arch.domain.ArchitectureDataStructure;
import net.trilogy.arch.facade.FilesFacade;

public class DiffCommandE2ETest {
    @Rule
    public final ErrorCollector collector = new ErrorCollector();

    final PrintStream originalOut = System.out;
    final PrintStream originalErr = System.err;
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    final ByteArrayOutputStream err = new ByteArrayOutputStream();

    private Application app;
    private File rootDir;
    private GitInterface mockedGitInterface;
    private File outputDirParent;

    @Before
    public void setUp() throws Exception {
        out.reset();
        err.reset();
        System.setOut(new PrintStream(out));
        System.setErr(new PrintStream(err));

        rootDir = new File(
                getClass().getResource(TestHelper.ROOT_PATH_TO_TEST_DIFF_COMMAND).getPath());
        outputDirParent = Files.createTempDirectory("aac").toFile();

        mockedGitInterface = mock(GitInterface.class);
        app = Application.builder().gitInterface(mockedGitInterface).build();
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.forceDelete(outputDirParent);
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    public void shouldFailIfCannotCreateDirectory() throws Exception {
        // GIVEN
        final String architectureAsString = new FilesFacade()
                .readString(rootDir.toPath().resolve("product-architecture.yml"))
                .replaceAll("id: \"16\"", "id: \"116\"");
        final ArchitectureDataStructure dataStructure = new ArchitectureDataStructureObjectMapper()
                .readValue(architectureAsString);
        when(mockedGitInterface.load("master",
                rootDir.toPath().resolve("product-architecture.yml"))).thenReturn(dataStructure);

        // WHEN
        final int status = execute(app, "diff -b master " + rootDir.getAbsolutePath() + " -o "
                + outputDirParent.getAbsolutePath());

        // THEN
        collector.checkThat(status, not(equalTo(0)));
        collector.checkThat(err.toString(), containsString("Unable to create output directory"));
        collector.checkThat(out.toString(), equalTo(""));
    }

    @Test
    public void shouldCreateArchitectureDiffSvg() throws Exception {
        // GIVEN
        final String architectureAsString = new FilesFacade()
                .readString(rootDir.toPath().resolve("product-architecture.yml"))
                .replaceAll("id: \"16\"", "id: \"116\"");
        final ArchitectureDataStructure dataStructure = new ArchitectureDataStructureObjectMapper()
                .readValue(architectureAsString);
        when(mockedGitInterface.load("master",
                rootDir.toPath().resolve("product-architecture.yml"))).thenReturn(dataStructure);

        // WHEN
        final var outputPath = outputDirParent.toPath().resolve("ourOutputDir").toAbsolutePath();
        final Integer status = execute(app,
                "diff -b master " + rootDir.getAbsolutePath() + " -o " + outputPath.toString());

        // THEN
        collector.checkThat(out.toString(),
                equalTo("SVG files created in " + outputPath.toString() + "\n"));
        collector.checkThat(Files.exists(outputPath.resolve("architecture-diff.svg")),
                equalTo(true));
        collector.checkThat(err.toString(), equalTo(""));
        collector.checkThat(status, equalTo(0));

        final var svgContent = Files.readString(outputPath.resolve("architecture-diff.svg"));
        collector.checkThat(svgContent, containsString("16"));
        collector.checkThat(svgContent, containsString("116"));
    }

    @Test
    public void shouldHandleIfGraphvizFails() throws Exception {
        // GIVEN
        final String architectureAsString = new FilesFacade()
                .readString(rootDir.toPath().resolve("product-architecture.yml"))
                .replaceAll("id: \"16\"", "id: \"116\"");
        final ArchitectureDataStructure dataStructure = new ArchitectureDataStructureObjectMapper()
                .readValue(architectureAsString);
        when(mockedGitInterface.load("master",
                rootDir.toPath().resolve("product-architecture.yml"))).thenReturn(dataStructure);
        
        final var mockedGraphvizInterface = mock(GraphvizInterface.class);
        doThrow(new RuntimeException("BOOM!")).when(mockedGraphvizInterface).render(any(), any());

        var app = Application.builder()
            .gitInterface(mockedGitInterface)
            .graphvizInterface(mockedGraphvizInterface)
            .build();

        // WHEN
        final var outputPath = outputDirParent.toPath().resolve("ourOutputDir").toAbsolutePath();
        final Integer status = execute(app,
                "diff -b master " + rootDir.getAbsolutePath() + " -o " + outputPath.toString());

        // THEN
        collector.checkThat(status, not(equalTo(0)));
        collector.checkThat(err.toString(), containsString("Unable to render SVG"));
        collector.checkThat(out.toString(), equalTo(""));
    }
}
