package net.trilogy.arch.domain.architectureUpdate;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class TDD {

    private final TDD.Id id;
    private final String text;

    public TDD(Id id, String text) {
        this.id = id;
        this.text = text;
    }

    static TDD blank() {
        return new TDD(Id.blank(), "[SAMPLE TDD TEXT]");
    }

    @Getter
    @EqualsAndHashCode
    @AllArgsConstructor
    public static class Id {
        @JsonValue
        private final String id;

        static Id blank() {
            return new Id("[SAMPLE-TDD-ID]");
        }
    }

    @Getter
    @EqualsAndHashCode
    @AllArgsConstructor
    public static class ComponentReference {
        private final String id;

        static ComponentReference blank() {
            return new ComponentReference("[SAMPLE-COMPONENT-ID]");
        }

        @JsonValue
        public String jsonRender() {
            return "Component-" + id;
        }
    }
}
