package net.trilogy.arch.domain.architectureUpdate;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode
public class FeatureStory {
    private final String title;
    private final Jira jira;

    @JsonProperty(value = "tdd-references")
    private final List<Tdd.Id> tddReferences;

    @JsonProperty(value = "functional-requirement-references")
    private final List<FunctionalRequirement.Id> requirementReferences;

    public FeatureStory(String title, Jira jira, List<Tdd.Id> tddReferences, List<FunctionalRequirement.Id> requirementReferences) {
        this.title = title;
        this.jira = jira;
        this.tddReferences = tddReferences;
        this.requirementReferences = requirementReferences;
    }

    public static FeatureStory blank() {
        return new FeatureStory(
                "[SAMPLE FEATURE STORY TITLE]",
                Jira.blank(),
                List.of(Tdd.Id.blank()),
                List.of(FunctionalRequirement.Id.blank())
        );
    }
}
