package net.trilogy.arch.domain.architectureUpdate;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class Tdd {

    private final Tdd.Id id;
    private final String text;

    public Tdd(Id id, String text) {
        this.id = id;
        this.text = text;
    }

    public static Tdd blank() {
        return new Tdd(Id.blank(), "[SAMPLE TDD TEXT]");
    }

    @Getter
    @EqualsAndHashCode
    @AllArgsConstructor
    public static class Id {
        @JsonValue
        private final String id;

        public static Id blank() {
            return new Id("[SAMPLE-TDD-ID]");
        }
    }

    @Getter
    @EqualsAndHashCode
    @AllArgsConstructor
    public static class ComponentReference {
        private final String id;

        public static ComponentReference blank() {
            return new ComponentReference("[SAMPLE-COMPONENT-ID]");
        }

        @JsonValue
        public String asJson() {
            return "Component-" + id;
        }
    }
}
