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
public class Decision {
    private final String text;

    @JsonProperty(value = "tdd-references")
    private final List<Tdd.Id> tddReferences;

    public Decision(String text, List<Tdd.Id> tddReferences) {
        this.text = text;
        this.tddReferences = tddReferences;
    }

    public static Decision blank() {
        return new Decision("[SAMPLE DECISION TEXT]", List.of(Tdd.Id.blank()));
    }

    @Getter
    @ToString
    @EqualsAndHashCode
    @AllArgsConstructor
    public static class Id{
        @JsonValue
        private final String id;

        public static Id blank() {
            return new Id("[SAMPLE-DECISION-ID]");
        }
    }
}
