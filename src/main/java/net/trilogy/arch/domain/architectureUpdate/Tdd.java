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
public class Tdd {
    @JsonProperty(value = "text")
    private final String text;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Tdd(
            @JsonProperty("text") String text
    ) {
        this.text = text;
    }

    public static Tdd blank() {
        return new Tdd("[SAMPLE TDD TEXT]");
    }

    @EqualsAndHashCode
    public static class Id implements EntityReference {
        @JsonValue
        private final String id;

        public Id(String id) {
            this.id = id;
        }

        public static Id blank() {
            return new Id("[SAMPLE-TDD-ID]");
        }

        public String toString() {
            return this.id;
        }
    }

    @EqualsAndHashCode
    public static class ComponentReference implements EntityReference {
        private final String id;

        public ComponentReference(String id) {
            this.id = id.replaceFirst("Component-", "");
        }

        public static ComponentReference blank() {
            return new ComponentReference("[SAMPLE-COMPONENT-ID]");
        }

        @JsonValue
        public String asJson() {
            return "Component-" + id;
        }

        public String toString() {
            return this.id;
        }
    }
}
