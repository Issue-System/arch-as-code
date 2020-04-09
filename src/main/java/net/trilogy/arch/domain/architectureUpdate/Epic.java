package net.trilogy.arch.domain.architectureUpdate;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@Getter
@EqualsAndHashCode
public class Epic {
    private final String title;
    private final Jira jira;
    private final List<Capability> capabilities;

    @Builder
    public Epic(String title, Jira jira, List<Capability> capabilities) {
        this.title = title;
        this.jira = jira;
        this.capabilities = capabilities;
    }

    public static Epic blank() {
        return new Epic("[SAMPLE EPIC TITLE]",
                Jira.blank(),
                List.of(
                        new Capability(
                                Jira.blank(),
                                List.of(Tdd.Id.blank()),
                                List.of(Requirement.Id.blank())
                        )
                )
        );
    }
}
