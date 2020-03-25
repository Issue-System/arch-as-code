package net.trilogy.arch.domain;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ArchitectureUpdate {
    private final String name;
    private final String milestone;
    private final List<Person> authors;
    private final List<Person> PCAs;
}
