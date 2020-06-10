package net.trilogy.arch.services;

import java.util.Set;

import net.trilogy.arch.domain.diff.Diff;
import net.trilogy.arch.domain.c4.Entity;
import net.trilogy.arch.domain.diff.Diffable;
import net.trilogy.arch.domain.diff.DiffableEntity;
import net.trilogy.arch.domain.diff.DiffableRelationship;

public class DiffToDotCalculator {

    public static String toDot(String title, Set<Diff> diffs) {
        final var dot = new Dot();
        dot.add(0, "digraph " + title + " {");
        dot.add(0, "}");
        return dot.toString();
    }

    private static class Dot{
        private final StringBuilder builder = new StringBuilder();

        public String toString() {
            return builder.toString();
        }

        public void add(int indentationLevel, String line) {
            builder.append("    ".repeat(indentationLevel)).append(line).append("\n");
        }
    }

    static String getDotShape(DiffableEntity entity) { 
        return "Mrecord";
    }

    static String getDotColor(Diff diff) { 
        return "blue";
    }

    static String toDot(Diff diff){ 
        if(diff.getElement() instanceof DiffableEntity) {
            final var entity = (DiffableEntity) diff.getElement();
            return entity.getId() + 
                " [label=\"" + entity.getName() +
                "\", color=" + getDotColor(diff) +
                ", fontcolor=" + getDotColor(diff) +
                ", shape=" + getDotShape(entity) +
                "];";
        }
        final var relationship = (DiffableRelationship) diff.getElement();
        return relationship.getSourceId() + " -> " + relationship.getRelationship().getWithId() + 
            " [label=\"" + relationship.getName() +
            "\", color=" + getDotColor(diff) +
            ", fontcolor=" + getDotColor(diff) +
            "];";
    }
}
