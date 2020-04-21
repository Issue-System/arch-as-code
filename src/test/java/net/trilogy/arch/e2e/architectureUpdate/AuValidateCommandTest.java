package net.trilogy.arch.e2e.architectureUpdate;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;

import static net.trilogy.arch.TestHelper.execute;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class AuValidateCommandTest {
    private File rootDir;

    @Before
    public void setUp() throws Exception {
        rootDir = Files.createTempDirectory("aac").toFile();

        execute("au", "init", "-c", "c", "-p", "p", "-s", "s", rootDir.getAbsolutePath());
    }

    @Test
    public void shouldExitWithHappyStatus() throws Exception {
        Integer status1 = execute("architecture-update", "validate", rootDir.getAbsolutePath());
        assertThat(status1, equalTo(0));

        Integer status2 = execute("au", "validate", rootDir.getAbsolutePath());
        assertThat(status2, equalTo(0));
    }
}
