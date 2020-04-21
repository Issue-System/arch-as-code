package net.trilogy.arch.domain.architectureUpdate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class FunctionalRequirement {
    @JsonProperty(value = "text") private final String text;
    @JsonProperty(value = "source") private final String source;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public FunctionalRequirement(
            @JsonProperty("text") String text,
            @JsonProperty("source") String source
    ) {
        this.text = text;
        this.source = source;
    }

    public static FunctionalRequirement blank() {
        return new FunctionalRequirement("[SAMPLE REQUIREMENT TEXT]", "[SAMPLE REQUIREMENT SOURCE TEXT]");
    }

    @Getter
    @ToString
    @EqualsAndHashCode
    public static class Id {
        @JsonValue
        private final String id;

        public Id(String id) {
            this.id = id;
        }

        public static Id blank() {
            return new Id("[SAMPLE-REQUIREMENT-ID]");
        }
    }
}
