package net.trilogy.arch.domain;

import java.util.ArrayList;
import java.util.List;

public class ArchitectureUpdate {
    private final String name;
    private final String milestone;
    private final List<Person> authors;
    private final List<Person> PCAs;

    public ArchitectureUpdate(String name, String milestone, List<Person> authors, List<Person> PCAs) {
        this.name = name;
        this.milestone = milestone;
        this.authors = authors != null ? new ArrayList<>(authors) : new ArrayList<>();
        this.PCAs = PCAs != null ? new ArrayList<>(PCAs) : new ArrayList<>();
    }
}
