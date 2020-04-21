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
    @JsonProperty(value = "id") private final Tdd.Id id;
    @JsonProperty(value = "text") private final String text;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Tdd(
            @JsonProperty("id") Id id,
            @JsonProperty("text") String text
    ) {
        this.id = id;
        this.text = text;
    }

    public static Tdd blank() {
        return new Tdd(Id.blank(), "[SAMPLE TDD TEXT]");
    }

    @Getter
    @EqualsAndHashCode
    public static class Id {
        @JsonValue
        private final String id;

        public Id(String id) {
            this.id = id;
        }

        public static Id blank() {
            return new Id("[SAMPLE-TDD-ID]");
        }
    }

    @Getter
    @EqualsAndHashCode
    public static class ComponentReference {
        @JsonProperty(value = "id") private final String id;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public ComponentReference(
                @JsonProperty("id") String id
        ) {
            this.id = id;
        }

        public static ComponentReference blank() {
            return new ComponentReference("[SAMPLE-COMPONENT-ID]");
        }

        @JsonValue
        public String asJson() {
            return "Component-" + id;
        }
    }
}
