package net.trilogy.arch.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.fail;

import java.util.Set;

import net.trilogy.arch.domain.diff.DiffableEntity;
import org.junit.Test;

import static net.trilogy.arch.ArchitectureDataStructureHelper.createPerson;
import net.trilogy.arch.domain.diff.Diff;

public class DiffToDotCalculatorTest {

    @Test
    public void shouldGenerateEmptyDot() {
        var actual = DiffToDotCalculator.toDot("title", Set.of());

        var expected = new StringBuilder();
        appendln(expected, "digraph title {");
        appendln(expected, "    node [shape=Mrecord];");
        appendln(expected, "");
        appendln(expected, "}");

        assertThat(actual, equalTo(expected.toString()));
    }

    @Test
    public void shouldHandleEntityWithNoChange() {
        var actual = DiffToDotCalculator.toDot("title", Set.of(
            new Diff(
                    new DiffableEntity(createPerson("4")),
                    new DiffableEntity(createPerson("4"))
            )
        ));

        var expected = new StringBuilder();
        appendln(expected, "digraph title {");
        appendln(expected, "    node [shape=Mrecord];");
        appendln(expected, "");
        appendln(expected, "    4 [label=\"person-4\"];");
        appendln(expected, "}");

        assertThat(actual, equalTo(expected.toString()));
    }

    private void appendln(StringBuilder builder, String line){
        builder.append(line).append("\n");
    }

}
