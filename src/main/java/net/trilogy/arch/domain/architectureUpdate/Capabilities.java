package net.trilogy.arch.domain.architectureUpdate;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

import java.util.List;

import static net.trilogy.arch.domain.architectureUpdate.ArchitectureUpdate.copyList;

@EqualsAndHashCode
public class Capabilities {
    private final String E2Es;
    private final String ACCs;
    private final List<Story> stories;

    public Capabilities(String E2Es, String ACCs, List<Story> stories) {
        this.E2Es = E2Es;
        this.ACCs = ACCs;
        this.stories = copyList(stories);
    }

    @EqualsAndHashCode
    public static class Story {
        @JsonProperty(value = "TDDs")
        private final List<String> tddReferences;
        @JsonProperty(value = "requirements")
        private final List<String> requirementReferences;

        public Story(List<String> tddReferences, List<String> requirementReferences) {
            this.tddReferences = copyList(tddReferences);
            this.requirementReferences = copyList(requirementReferences);
        }
    }
}
