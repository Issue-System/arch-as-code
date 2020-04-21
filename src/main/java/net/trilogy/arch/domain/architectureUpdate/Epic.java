package net.trilogy.arch.domain.architectureUpdate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Epic {
    @JsonProperty(value = "title") private final String title;
    @JsonProperty(value = "jira") private final Jira jira;

    @Builder
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Epic(
            @JsonProperty("title") String title,
            @JsonProperty("jira") Jira jira
    ) {
        this.title = title;
        this.jira = jira;
    }

    public static Epic blank() {
        return new Epic("[SAMPLE EPIC TITLE]", Jira.blank());
    }
}
