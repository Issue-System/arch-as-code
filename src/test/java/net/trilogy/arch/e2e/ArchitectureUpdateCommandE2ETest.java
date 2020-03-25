package net.trilogy.arch.e2e;

import org.junit.Test;

import static net.trilogy.arch.TestHelper.execute;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class ArchitectureUpdateCommandE2ETest {
    @Test
    public void shouldExitWithHappyStatus() {
        assertThat(execute("au", "init"), is(equalTo(0)));
        assertThat(execute("architecture-update", "init"), is(equalTo(0)));
        assertThat(execute("au", "initialize"), is(equalTo(0)));
        assertThat(execute("architecture-update", "initialize"), is(equalTo(0)));
    }
}
