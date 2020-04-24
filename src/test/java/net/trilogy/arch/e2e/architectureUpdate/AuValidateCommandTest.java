package net.trilogy.arch.e2e.architectureUpdate;

import net.trilogy.arch.TestHelper;
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
import static org.hamcrest.Matchers.not;

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
        rootDir = new File(getClass().getResource(TestHelper.ROOT_PATH_TO_TEST_AU_VALIDATION).getPath());
        out.reset();
        err.reset();
        System.setOut(new PrintStream(out));
        System.setErr(new PrintStream(err));
    }

    @After
    public void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    public void shouldExitWithHappyStatus_short() throws Exception {
        Integer status = execute("au", "validate", "blank.yml", rootDir.getAbsolutePath());
        collector.checkThat(status, equalTo(0));
        collector.checkThat(
                out.toString(),
                containsString("Success, no errors found.")
        );
    }

    public void shouldExitWithHappyStatus_long() throws Exception {
        Integer status = execute("architecture-update", "validate", "blank.yml", rootDir.getAbsolutePath());
        collector.checkThat(status, equalTo(0));
        collector.checkThat(
                out.toString(),
                containsString("Success, no errors found.")
        );
    }

    @Test
    public void shouldValidateAu() throws Exception {
        Integer status = execute("au", "validate", "missing_decision_tdds_blank.yml", rootDir.getAbsolutePath());
        collector.checkThat(status, not(equalTo(0)));

        collector.checkThat(
                err.toString(),
                containsString("Decision \"[SAMPLE-DECISION-ID]\" must have at least one TDD reference.")
        );
    }

}
