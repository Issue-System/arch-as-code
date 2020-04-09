package net.trilogy.arch.domain.architectureUpdate;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode
@RequiredArgsConstructor
public class Person {
    private final String name;
    private final String email;

    public static Person blank() {
        return new Person("[SAMPLE PERSON NAME]", "[SAMPLE PERSON EMAIL]");
    }
}
