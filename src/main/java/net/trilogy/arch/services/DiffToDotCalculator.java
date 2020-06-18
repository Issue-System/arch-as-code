package net.trilogy.arch.services;

import com.google.common.annotations.VisibleForTesting;
import net.trilogy.arch.domain.c4.C4Type;
import net.trilogy.arch.domain.diff.Diff;
import net.trilogy.arch.domain.diff.DiffableEntity;
import net.trilogy.arch.domain.diff.DiffableRelationship;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Set;

public class DiffToDotCalculator {

    public static String toDot(String title, Collection<Diff> diffs, @Nullable Diff parentEntityDiff, String linkPrefix) {
        final var dot = new Dot();
        dot.add(0, "digraph \"" + title + "\" {");
        dot.add(1, "graph [rankdir=LR];");
        dot.add(0, "");
        if (parentEntityDiff != null) {
            dot.add(1, "subgraph \"cluster_" + parentEntityDiff.getElement().getId() + "\" {");
            dot.add(2, "label=\"" + parentEntityDiff.getElement().getName() + "\";");
            parentEntityDiff.getDescendants()
                    .stream()
                    .filter(it -> diffs.stream().anyMatch(diff -> it.getId().equals(diff.getElement().getId())))
                    .filter(it -> !it.getType().equals(C4Type.relationship))
                    .map(it -> "\"" + it.getId() + "\";")
                    .forEach(it -> dot.add(2, it));
            dot.add(1, "}");
            dot.add(0, "");
        }
        diffs.stream()
                .map(diff -> toDot(diff, linkPrefix))
                .forEach(line -> dot.add(1, line));
        dot.add(0, "}");
        return dot.toString();
    }

    private static class Dot {
        private final StringBuilder builder = new StringBuilder();

        public String toString() {
            return builder.toString();
        }

        public void add(int indentationLevel, String line) {
            builder.append("    ".repeat(indentationLevel)).append(line).append("\n");
        }
    }

    @VisibleForTesting
    static String getDotLabel(DiffableEntity entity) {
        return "<<TABLE BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\"><TR><TD>"+entity.getName()+"</TD></TR><TR><TD>"+entity.getType()+"</TD></TR><TR><TD>"+getPath(entity)+"</TD></TR></TABLE>>";
    }

    @VisibleForTesting
    static String getDotColor(Diff diff) {
        switch (diff.getStatus()) {
            case CREATED:
                return "darkgreen";
            case DELETED:
                return "red";
            case UPDATED:
                return "blue";
            case NO_UPDATE_BUT_CHILDREN_UPDATED:
                return "blueviolet";
            case NO_UPDATE:
                return "black";
            default:
                return "black";
        }
    }

    @VisibleForTesting
    static String getUrl(Diff diff, String linkPrefix) {
        boolean shouldHaveDiagram = diff.getDescendants().stream()
                .anyMatch(it -> Set.of(C4Type.component, C4Type.container)
                        .contains(it.getType()));

        if (!shouldHaveDiagram) return "";

        return linkPrefix + diff.getElement().getId() + ".svg";
    }

    private static String getPath(DiffableEntity entity) {
        return entity.getEntity().getPath() == null ? "" : entity.getEntity().getPath().getPath();
    }

    @VisibleForTesting
    static String toDot(Diff diff, String linkPrefix) {
        if (diff.getElement() instanceof DiffableEntity) {
            final var entity = (DiffableEntity) diff.getElement();
            return "\"" + entity.getId() + "\" " +
                    "[label=" + getDotLabel(entity) +
                    ", color=" + getDotColor(diff) +
                    ", fontcolor=" + getDotColor(diff) +
                    ", shape=plaintext" +
                    ", URL=\"" + getUrl(diff, linkPrefix) + "\"" +
                    "];";
        }
        final var relationship = (DiffableRelationship) diff.getElement();
        return "\"" +
                relationship.getSourceId() + "\" -> \"" + relationship.getRelationship().getWithId() +
                "\" " +
                "[label=\"" + relationship.getName() +
                "\", color=" + getDotColor(diff) +
                ", fontcolor=" + getDotColor(diff) +
                "];";
    }
}
