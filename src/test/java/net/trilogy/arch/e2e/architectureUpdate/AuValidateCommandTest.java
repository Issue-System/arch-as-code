package net.trilogy.arch.e2e.architectureUpdate;

import static net.trilogy.arch.TestHelper.execute;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import net.trilogy.arch.TestHelper;

public class AuValidateCommandTest {
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
        rootDir = new File(getClass().getResource(TestHelper.ROOT_PATH_TO_TEST_AU_VALIDATION).getPath());
    }

    @After
    public void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    public void shouldBeFullyValid() throws Exception {
        var auPath = rootDir.getAbsolutePath() + "/architecture-updates/blank.yml";
        Integer status = execute("au", "validate", auPath, rootDir.getAbsolutePath());
        collector.checkThat(status, equalTo(0));
        collector.checkThat(out.toString(), containsString("Success, no errors found."));
        collector.checkThat(err.toString(), equalTo(""));
    }

    @Test
    public void shouldBeTDDValid() throws Exception {
        var auPath = rootDir.getAbsolutePath() + "/architecture-updates/invalid_capabilities.yml";
        Integer status = execute("architecture-update", "validate", "-t", auPath, rootDir.getAbsolutePath());
        collector.checkThat(status, equalTo(0));
        collector.checkThat(out.toString(), containsString("Success, no errors found."));
        collector.checkThat(err.toString(), equalTo(""));
    }

    @Test
    public void shouldBeStoryValid() throws Exception {
        var auPath = rootDir.getAbsolutePath() + "/architecture-updates/invalid_tdds.yml";
        Integer status = execute("architecture-update", "validate", "-s", auPath, rootDir.getAbsolutePath());
        collector.checkThat(status, equalTo(0));
        collector.checkThat(out.toString(), containsString("Success, no errors found."));
        collector.checkThat(err.toString(), equalTo(""));
    }

    @Test
    public void shouldPresentErrorsNicely() throws Exception {
        var auPath = rootDir.getAbsolutePath() + "/architecture-updates/both_invalid.yml";
        Integer status = execute("au", "validate", auPath, rootDir.getAbsolutePath());
        collector.checkThat(status, not(equalTo(0)));
        collector.checkThat(out.toString(), equalTo(""));

        collector.checkThat(
                err.toString(),
                equalTo("" +
                        "Decision Missing TDD:\n" +
                        "    Decision \"[SAMPLE-DECISION-ID]\" must have at least one TDD reference.\n" +
                        "Invalid Component Reference:\n" +
                        "    Component id \"[INVALID-COMPONENT-ID]\" does not exist.\n" +
                        "Story Missing TDD:\n" +
                        "    Story \"[SAMPLE FEATURE STORY TITLE]\" must have at least one TDD reference.\n" +
                        "Missing Capability:\n" +
                        "    TDD \"[SAMPLE-TDD-ID]\" needs to be referenced in a story.\n" +
                        ""
                )
        );
    }

    @Test
    public void shouldBeFullyInvalid() throws Exception {
        var auPath = rootDir.getAbsolutePath() + "/architecture-updates/both_invalid.yml";
        Integer status = execute("au", "validate", auPath, rootDir.getAbsolutePath());
        collector.checkThat(status, not(equalTo(0)));
        collector.checkThat(out.toString(), equalTo(""));

        collector.checkThat(
                err.toString(),
                containsString("Decision \"[SAMPLE-DECISION-ID]\" must have at least one TDD reference.")
        );
        collector.checkThat(
                err.toString(),
                containsString("TDD \"[SAMPLE-TDD-ID]\" needs to be referenced in a story.")
        );
        collector.checkThat(
                err.toString(),
                containsString("Component id \"[INVALID-COMPONENT-ID]\" does not exist.")
        );
    }

    @Test
    public void shouldBeTddInvalid() throws Exception {
        var auPath = rootDir.getAbsolutePath() + "/architecture-updates/both_invalid.yml";
        Integer status = execute("au", "validate", "--TDDs", auPath, rootDir.getAbsolutePath());
        collector.checkThat(status, not(equalTo(0)));
        collector.checkThat(out.toString(), equalTo(""));

        collector.checkThat(
                err.toString(),
                containsString("Decision \"[SAMPLE-DECISION-ID]\" must have at least one TDD reference.")
        );
        collector.checkThat(
                err.toString(),
                not(containsString("TDD \"[SAMPLE-TDD-ID]\" needs to be referenced in a story."))
        );
        collector.checkThat(
                err.toString(),
                containsString("Component id \"[INVALID-COMPONENT-ID]\" does not exist.")
        );
    }

    @Test
    public void shouldBeStoryInvalid() throws Exception {
        var auPath = rootDir.getAbsolutePath() + "/architecture-updates/both_invalid.yml";
        Integer status = execute("au", "validate", "--stories", auPath, rootDir.getAbsolutePath());
        collector.checkThat(status, not(equalTo(0)));
        collector.checkThat(out.toString(), equalTo(""));

        collector.checkThat(
                err.toString(),
                not(containsString("Component id \"[INVALID-COMPONENT-ID]\" does not exist."))
        );
        collector.checkThat(
                err.toString(),
                not(containsString("Decision \"[SAMPLE-DECISION-ID]\" must have at least one TDD reference."))
        );
        collector.checkThat(
                err.toString(),
                containsString("TDD \"[SAMPLE-TDD-ID]\" needs to be referenced in a story.")
        );
    }

    @Test
    public void shouldFindAUStructureErrors() throws Exception {
        var auPath = rootDir.getAbsolutePath() + "/architecture-updates/invalid_structure.yml";
        Integer status = execute("au", "validate", auPath, rootDir.getAbsolutePath());
        collector.checkThat(status, not(equalTo(0)));
        collector.checkThat(out.toString(), equalTo(""));

        collector.checkThat(
                err.toString(),
                containsString("Invalid structure")
        );
    }
}
