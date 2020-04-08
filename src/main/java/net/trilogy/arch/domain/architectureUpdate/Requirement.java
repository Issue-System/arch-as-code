package net.trilogy.arch.domain.architectureUpdate;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class Requirement {
    @Getter
    @ToString
    @EqualsAndHashCode
    @AllArgsConstructor
    public static class Id{
        @JsonValue
        private final String id;
    }

    @JsonValue
    private final String requirement;
}
