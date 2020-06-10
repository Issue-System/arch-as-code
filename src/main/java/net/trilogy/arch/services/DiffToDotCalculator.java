package net.trilogy.arch.services;

import java.util.Set;

import net.trilogy.arch.domain.Diff;
import net.trilogy.arch.domain.c4.Entity;

public class DiffToDotCalculator {

    public static String toDot(String title, Set<Diff> diffs) {
        final var dot = new StringBuilder();
        appendln(dot, "digraph " + title + " {");
        appendln(dot, "    node [shape=Mrecord];");
        appendln(dot, "");
        for(var diff : diffs){
            if(diff.getElement() instanceof Entity){
                append(dot, (Entity) diff.getElement());
            }
        }
        appendln(dot, "}");
        return dot.toString();
    }

    private static void append(StringBuilder dot, Entity element) {
        appendln(dot, "    " + element.getId() + " [label=\"" + element.getName() + "\"];");
    }

    private static void appendln(StringBuilder dot, String line){
        dot.append(line).append("\n");
    }
}
