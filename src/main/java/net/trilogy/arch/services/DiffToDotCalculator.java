package net.trilogy.arch.services;

import java.util.Set;

import net.trilogy.arch.domain.Diff;
import net.trilogy.arch.domain.c4.Entity;

public class DiffToDotCalculator {

    public static String toDot(String title, Set<Diff> diffs) {
        final var dot = new Dot();
        dot.add(0, "digraph " + title + " {");
        dot.add(1, "node [shape=Mrecord];");
        dot.add(0, "");
        for(var diff : diffs){
            if(diff.getElement() instanceof Entity){
                dot.add(1, (Entity) diff.getElement());
            }
        }
        dot.add(0, "}");
        return dot.toString();
    }

    private static class Dot{
        private final StringBuilder builder = new StringBuilder();

        public String toString() {
            return builder.toString();
        }

        private void add(int indentationLevel, Entity element) {
            add(indentationLevel, element.getId() + " [label=\"" + element.getName() + "\"];");
        }

        public void add(int indentationLevel, String line) {
            builder.append("    ".repeat(indentationLevel)).append(line).append("\n");
        }
    }
}
