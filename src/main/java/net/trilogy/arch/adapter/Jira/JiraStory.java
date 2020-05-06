package net.trilogy.arch.adapter.Jira;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import net.trilogy.arch.domain.architectureUpdate.ArchitectureUpdate;
import net.trilogy.arch.domain.architectureUpdate.FeatureStory;
import net.trilogy.arch.domain.architectureUpdate.FunctionalRequirement;
import net.trilogy.arch.domain.architectureUpdate.Tdd;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class JiraStory {
    private final String title;
    private final List<JiraTdd> tdds;
    private final List<JiraFunctionalRequirement> functionalRequirements;

    public JiraStory(ArchitectureUpdate au, FeatureStory featureStory) {
        this.title = featureStory.getTitle();
        this.tdds = getTdds(au, featureStory);
        this.functionalRequirements = getFunctionalRequirements(au, featureStory);
    }

    private List<JiraFunctionalRequirement> getFunctionalRequirements(ArchitectureUpdate au, FeatureStory featureStory) {
        return featureStory
                .getRequirementReferences()
                .stream()
                .map(reqId -> new JiraFunctionalRequirement(reqId, au.getFunctionalRequirements().get(reqId)))
                .collect(Collectors.toList());
    }

    private List<JiraTdd> getTdds(ArchitectureUpdate au, FeatureStory featureStory) {
        return featureStory.getTddReferences().stream()
                .map(tddId ->
                        au.getTDDs()
                                .entrySet()
                                .stream()
                                .filter(componentEntry -> componentEntry.getValue().containsKey(tddId))
                                .map(componentEntry -> new JiraTdd(
                                        tddId,
                                        componentEntry.getValue().get(tddId),
                                        componentEntry.getKey()
                                ))
                                .findAny()
                                .orElseThrow()
                )
                .collect(Collectors.toList());
    }

    @ToString
    @EqualsAndHashCode
    @RequiredArgsConstructor
    public static class JiraTdd {
        private final Tdd.Id id;
        private final Tdd tdd;
        private final Tdd.ComponentReference component;

        public String getId() {
            return id.toString();
        }

        public String getComponent() {
            return component.toString();
        }

        public String getText() {
            return tdd.getText();
        }
    }

    @ToString
    @EqualsAndHashCode
    @RequiredArgsConstructor
    public static class JiraFunctionalRequirement {
        private final FunctionalRequirement.Id id;
        private final FunctionalRequirement functionalRequirement;

        public String getId() {
            return id.toString();
        }

        public String getText() {
            return functionalRequirement.getText();
        }

        public String getSource() {
            return functionalRequirement.getSource();
        }
    }
}
