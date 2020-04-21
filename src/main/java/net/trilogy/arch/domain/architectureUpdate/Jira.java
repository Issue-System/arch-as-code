package net.trilogy.arch.domain.architectureUpdate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Jira {
    @JsonProperty(value = "ticket") private final String ticket;
    @JsonProperty(value = "link") private final String link;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Jira(
            @JsonProperty("ticket") String ticket,
            @JsonProperty("link") String link
    ) {
        this.ticket = ticket;
        this.link = link;
    }

    public static Jira blank() {
        return new Jira("[SAMPLE JIRA TICKET]", "[SAMPLE JIRA TICKET LINK]");
    }
}
