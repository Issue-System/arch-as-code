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
public class TDD {

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

    @JsonValue
    private final String tdd;
}
