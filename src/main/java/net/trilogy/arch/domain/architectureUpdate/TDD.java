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

    @Getter
    @EqualsAndHashCode
    @AllArgsConstructor
    public static class Id {
        @JsonValue
        private final String id;
    }

    @Getter
    @EqualsAndHashCode
    @AllArgsConstructor
    public static class ComponentReference {
        private final String id;

        @JsonValue
        public String jsonRender() {
            return "Component-" + id;
        }
    }
}
