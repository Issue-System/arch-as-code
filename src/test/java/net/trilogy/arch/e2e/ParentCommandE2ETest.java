package net.trilogy.arch.e2e;

import net.trilogy.arch.commands.ParentCommand;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


public class ParentCommandE2ETest {

    @Test
    public void parent() {
        Integer statusCode = new ParentCommand().call();
        assertThat(statusCode, equalTo(0));
    }
}
