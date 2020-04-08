package net.trilogy.arch.domain.architectureUpdate;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class P1 {
    private final String link;
    private final Jira jira;

    @JsonProperty(value = "executive-summary")
    private final String executiveSummary;

    @Builder
    public P1(String link, Jira jira, String executiveSummary) {
        this.link = link;
        this.jira = jira;
        this.executiveSummary = executiveSummary;
    }
}
