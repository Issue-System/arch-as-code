package net.trilogy.arch.e2e.architectureUpdate;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import static net.trilogy.arch.TestHelper.execute;
import static org.hamcrest.Matchers.equalTo;

public class AuCommandTest {
    @Rule
    public final ErrorCollector collector = new ErrorCollector();

    @Test
    public void rootCommandShouldPrintUsage() throws Exception {
        // TODO: Assert that usage is shown
        collector.checkThat(
                execute("au"),
                equalTo(0)
        );
    }
}
