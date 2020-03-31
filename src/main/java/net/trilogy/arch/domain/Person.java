package net.trilogy.arch.domain;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode
@RequiredArgsConstructor
public class Person {
    private final String name;
    private final String email;
}
