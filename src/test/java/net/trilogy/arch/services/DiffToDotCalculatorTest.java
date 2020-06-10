package net.trilogy.arch.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.fail;

import java.util.Set;

import net.trilogy.arch.domain.diff.DiffableEntity;
import net.trilogy.arch.domain.diff.DiffableRelationship;

import org.junit.Test;

import static net.trilogy.arch.ArchitectureDataStructureHelper.createPerson;
import static net.trilogy.arch.ArchitectureDataStructureHelper.createRelationship;
import net.trilogy.arch.domain.diff.Diff;

public class DiffToDotCalculatorTest {

    @Test
    public void shouldGenerateEmptyGraph() {
        var actual = DiffToDotCalculator.toDot("title", Set.of());

        var expected = new StringBuilder();
        appendln(expected, "digraph title {");
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
    public void shouldCalculateUpdatedColor() {
        var actual = DiffToDotCalculator.getDotColor(
            new Diff(
                    new DiffableEntity(createPerson("4")),
                    new DiffableEntity(createPerson("4"))
            )
        );

        var expected = "blue";

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

        var expected = "4 -> 5 [label=\"d1\", color=blue, fontcolor=blue];";

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

        var expected = "4 [label=\"person-4\", color=blue, fontcolor=blue, shape=Mrecord];";

        assertThat(actual, equalTo(expected));
    }

    private void appendln(StringBuilder builder, String line){
        builder.append(line).append("\n");
    }

}
