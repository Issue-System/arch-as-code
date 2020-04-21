package net.trilogy.arch.e2e.architectureUpdate;

import net.trilogy.arch.TestHelper;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static net.trilogy.arch.TestHelper.execute;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

public class AuValidateCommandTest {
    private File rootDir;

    @Before
    public void setUp() {
        rootDir = new File(getClass().getResource(TestHelper.ROOT_PATH_TO_TEST_AU_VALIDATION).getPath());
    }

    @Test
    public void shouldExitWithHappyStatus() throws Exception {
        Integer status1 = execute("architecture-update", "validate", "blank.yml", rootDir.getAbsolutePath());
        assertThat(status1, equalTo(0));

        Integer status2 = execute("au", "validate", "blank.yml", rootDir.getAbsolutePath());
        assertThat(status2, equalTo(0));
    }

    @Test
    public void shouldValidateAu() throws Exception {
        Integer status = execute("architecture-update", "validate", "missing_decision_tdds_blank.yml", rootDir.getAbsolutePath());
        assertThat(status, not(equalTo(0)));
    }
}
