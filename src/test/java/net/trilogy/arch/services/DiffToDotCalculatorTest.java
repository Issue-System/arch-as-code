package net.trilogy.arch.services;

import net.trilogy.arch.ArchitectureDataStructureHelper;
import net.trilogy.arch.domain.diff.Diff;
import net.trilogy.arch.domain.diff.DiffableEntity;
import net.trilogy.arch.domain.diff.DiffableRelationship;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static net.trilogy.arch.ArchitectureDataStructureHelper.createPerson;
import static net.trilogy.arch.ArchitectureDataStructureHelper.createRelationship;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class DiffToDotCalculatorTest {

    @Test
    public void shouldGenerateEmptyGraph() {
        var actual = DiffToDotCalculator.toDot("title", Set.of(), null);
        var expected = new StringBuilder();
        appendln(expected, "digraph \"title\" {");
        appendln(expected, "    graph [rankdir=LR];");
        appendln(expected, "");
        appendln(expected, "}");

        assertThat(actual, equalTo(expected.toString()));
    }

    @Test
    public void shouldGenerateEmptyGraphWithParent() {
        var parentSystem = new Diff(
                new DiffableEntity(ArchitectureDataStructureHelper.createSystem("parent-system")),
                null
        );
        var actual = DiffToDotCalculator.toDot("title", Set.of(), parentSystem);
        var expected = new StringBuilder();
        appendln(expected, "digraph \"title\" {");
        appendln(expected, "    graph [rankdir=LR];");
        appendln(expected, "");
        appendln(expected, "    subgraph \"cluster_parent-system\" {");
        appendln(expected, "        label=\"system-parent-system\";");
        appendln(expected, "    }");
        appendln(expected, "");
        appendln(expected, "}");

        assertThat(actual, equalTo(expected.toString()));
    }

    @Test
    public void shouldGenerateGraph() {
        var actual = DiffToDotCalculator.toDot("title", List.of(
                new Diff(
                        new DiffableEntity(createPerson("1")),
                        new DiffableEntity(createPerson("1"))
                ),
                new Diff(
                        new DiffableRelationship("1", createRelationship("10", "3")),
                        new DiffableRelationship("1", createRelationship("10", "4"))
                )
        ), null);

        var expected = new StringBuilder();
        appendln(expected, "digraph \"title\" {");
        appendln(expected, "    graph [rankdir=LR];");
        appendln(expected, "");
        appendln(expected, "    \"1\" [label=\"person-1 | person\", color=black, fontcolor=black, shape=Mrecord];");
        appendln(expected, "    \"1\" -> \"4\" [label=\"d10\", color=blue, fontcolor=blue];");
        appendln(expected, "}");

        assertThat(actual, equalTo(expected.toString()));
    }

    @Test
    public void shouldRenderNodesInsideParent() {
        Diff diffOwnedByParent = new Diff(new DiffableEntity(createPerson("1")), null);
        Diff diffNotOwnedByParent = new Diff(new DiffableEntity(createPerson("2")), null);
        var diffs = List.of(
                diffOwnedByParent,
                diffNotOwnedByParent,
                new Diff(new DiffableRelationship("1", createRelationship("10", "2")), null)
        );
        var parentSystem = new Diff(
                new DiffableEntity(ArchitectureDataStructureHelper.createSystem("parent-system")),
                Set.of(diffOwnedByParent.getElement()),
                null,
                null
        );
        var actual = DiffToDotCalculator.toDot("title", diffs, parentSystem);
        var expected = new StringBuilder();
        appendln(expected, "digraph \"title\" {");
        appendln(expected, "    graph [rankdir=LR];");
        appendln(expected, "");
        appendln(expected, "    subgraph \"cluster_parent-system\" {");
        appendln(expected, "        label=\"system-parent-system\";");
        appendln(expected, "        \"1\";");
        appendln(expected, "    }");
        appendln(expected, "");
        appendln(expected, "    \"1\" [label=\"person-1 | person\", color=red, fontcolor=red, shape=Mrecord];");
        appendln(expected, "    \"2\" [label=\"person-2 | person\", color=red, fontcolor=red, shape=Mrecord];");
        appendln(expected, "    \"1\" -> \"2\" [label=\"d10\", color=red, fontcolor=red];");
        appendln(expected, "}");

        assertThat(actual, equalTo(expected.toString()));
    }


    @Test
    public void shouldCalculateRightShape() {
        var actual = DiffToDotCalculator.getDotShape(
                new DiffableEntity(createPerson("4"))
        );

        var expected = "Mrecord";

        assertThat(actual, equalTo(expected));
    }

    @Test
    public void shouldCalculateNoUpdateColor() {
        var actual = DiffToDotCalculator.getDotColor(
                new Diff(
                        new DiffableEntity(createPerson("4")),
                        new DiffableEntity(createPerson("4"))
                )
        );

        var expected = "black";

        assertThat(actual, equalTo(expected));
    }

    @Test
    public void shouldCalculateCreatedColor() {
        var actual = DiffToDotCalculator.getDotColor(
                new Diff(
                        null,
                        new DiffableEntity(createPerson("4"))
                )
        );

        var expected = "darkgreen";

        assertThat(actual, equalTo(expected));
    }

    @Test
    public void shouldCalculateUpdatedColor() {
        var actual = DiffToDotCalculator.getDotColor(
                new Diff(
                        new DiffableEntity(createPerson("4")),
                        new DiffableEntity(createPerson("5"))
                )
        );

        var expected = "blue";

        assertThat(actual, equalTo(expected));
    }

    @Test
    public void shouldCalculateNoUpdateChilderUpdatedColor() {
        var actual = DiffToDotCalculator.getDotColor(
                new Diff(
                        new DiffableEntity(createPerson("4")), Set.of(),
                        new DiffableEntity(createPerson("4")), Set.of(new DiffableEntity(createPerson("impossible-child")))
                )
        );

        var expected = "blueviolet";

        assertThat(actual, equalTo(expected));
    }

    @Test
    public void shouldCalculateDeletedColor() {
        var actual = DiffToDotCalculator.getDotColor(
                new Diff(
                        new DiffableEntity(createPerson("4")),
                        null
                )
        );

        var expected = "red";

        assertThat(actual, equalTo(expected));
    }

    @Test
    public void shouldGenerateRelationshipDotEntry() {
        var actual = DiffToDotCalculator.toDot(
                new Diff(
                        new DiffableRelationship(createPerson("4"), createRelationship("1", "5")),
                        new DiffableRelationship(createPerson("4"), createRelationship("1", "5"))
                )
        );

        var expected = "\"4\" -> \"5\" [label=\"d1\", color=black, fontcolor=black];";

        assertThat(actual, equalTo(expected));
    }

    @Test
    public void shouldGenerateEntityDotEntry() {
        var actual = DiffToDotCalculator.toDot(
                new Diff(
                        new DiffableEntity(createPerson("4")),
                        new DiffableEntity(createPerson("4"))
                )
        );

        var expected = "\"4\" [label=\"person-4 | person\", color=black, fontcolor=black, shape=Mrecord];";

        assertThat(actual, equalTo(expected));
    }

    private void appendln(StringBuilder builder, String line) {
        builder.append(line).append("\n");
    }

}
