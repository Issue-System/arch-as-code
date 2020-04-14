package net.trilogy.arch.domain.architectureUpdate;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class FunctionalRequirement {
    private final String text;

    private final String source;

    public FunctionalRequirement(String text, String source) {
        this.text = text;
        this.source = source;
    }

    public static FunctionalRequirement blank() {
        return new FunctionalRequirement("[SAMPLE REQUIREMENT TEXT]", "[SAMPLE REQUIREMENT SOURCE TEXT]");
    }

    @Getter
    @ToString
    @EqualsAndHashCode
    @AllArgsConstructor
    public static class Id {
        @JsonValue
        private final String id;

        public static Id blank() {
            return new Id("[SAMPLE-REQUIREMENT-ID]");
        }
    }
}
