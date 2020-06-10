package net.trilogy.arch.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.Set;

import org.junit.Test;

public class DiffToDotCalculatorTest {

    @Test
    public void shouldGenerateEmptyDot() {
        var actual = DiffToDotCalculator.toDot("title", Set.of());

        var expected = new StringBuilder();
        appendln(expected, "digraph title {");
        appendln(expected, "}");

        assertThat(actual, equalTo(expected.toString()));
    }

    private void appendln(StringBuilder builder, String line){
        builder.append(line).append("\n");
    }

}
