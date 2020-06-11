package net.trilogy.arch.e2e;

import net.trilogy.arch.Application;
import net.trilogy.arch.TestHelper;
import net.trilogy.arch.adapter.architectureYaml.ArchitectureDataStructureObjectMapper;
import net.trilogy.arch.adapter.git.GitInterface;
import net.trilogy.arch.adapter.google.GoogleDocsAuthorizedApiFactory;
import net.trilogy.arch.adapter.jira.JiraApiFactory;
import net.trilogy.arch.domain.ArchitectureDataStructure;
import net.trilogy.arch.facade.FilesFacade;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import static net.trilogy.arch.TestHelper.execute;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
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
    public void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    public void shouldPrintArchitectureDiff() throws Exception {
        // GIVEN
        final String architectureAsString = new FilesFacade().readString(rootDir.toPath().resolve("product-architecture.yml"))
                .replaceAll("id: \"16\"", "id: \"116\"");
        final ArchitectureDataStructure dataStructure = new ArchitectureDataStructureObjectMapper().readValue(architectureAsString);
        when(mockedGitInterface.load("master", rootDir.toPath().resolve("product-architecture.yml"))).thenReturn(dataStructure);

        // WHEN
        final Integer status = execute(app, "diff -b master " + rootDir.getAbsolutePath());

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
