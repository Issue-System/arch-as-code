package net.trilogy.arch.domain.architectureUpdate;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode
public class Capability {
    private final Jira jira;

    @JsonProperty(value = "tdd-references")
    private final List<TDD.Id> tddReferences;

    @JsonProperty(value = "requirement-references")
    private final List<Requirement.Id> requirementReferences;

    public Capability(Jira jira, List<TDD.Id> tddReferences, List<Requirement.Id> requirementReferences) {
        // TODO: copy lists properly
        this.jira = jira;
        this.tddReferences = tddReferences;
        this.requirementReferences = requirementReferences;
    }
}
