package net.trilogy.arch.domain.architectureUpdate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@EqualsAndHashCode
public class Requirement {
    private final String text;

    @JsonProperty(value = "tdd-references")
    private final List<TDD.Id> tddReferences;

    public Requirement(String text, List<TDD.Id> tddReferences) {
        // TODO: copy lists correctly
        this.text = text;
        this.tddReferences = tddReferences;
    }

    @Getter
    @ToString
    @EqualsAndHashCode
    @AllArgsConstructor
    public static class Id{
        @JsonValue
        private final String id;
    }
}
