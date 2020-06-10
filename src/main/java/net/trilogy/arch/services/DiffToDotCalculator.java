package net.trilogy.arch.services;

import java.util.Set;

import net.trilogy.arch.domain.Diff;

public class DiffToDotCalculator {

    public static Object toDot(String title, Set<Diff> diffs) {
        return "digraph " + title + " {\n}\n";
    }
}
