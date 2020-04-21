package net.trilogy.arch.domain.architectureUpdate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class P1 {
    @JsonProperty(value = "link") private final String link;
    @JsonProperty(value = "jira") private final Jira jira;
    @JsonProperty(value = "executive-summary") private final String executiveSummary;

    @Builder
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public P1(
            @JsonProperty("link") String link,
            @JsonProperty("jira") Jira jira,
            @JsonProperty("executive-summary") String executiveSummary
    ) {
        this.link = link;
        this.jira = jira;
        this.executiveSummary = executiveSummary;
    }

    public static P1 blank() {
        return new P1("[SAMPLE LINK TO P1]", Jira.blank(), "[SAMPLE EXECUTIVE SUMMARY]");
    }
}
