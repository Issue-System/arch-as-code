package net.trilogy.arch.adapter.Jira;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import net.trilogy.arch.domain.ArchitectureDataStructure;
import net.trilogy.arch.domain.architectureUpdate.ArchitectureUpdate;
import net.trilogy.arch.domain.architectureUpdate.FeatureStory;
import net.trilogy.arch.domain.architectureUpdate.FunctionalRequirement;
import net.trilogy.arch.domain.architectureUpdate.Tdd;

import java.util.ArrayList;
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

    public JiraStory(ArchitectureUpdate au, ArchitectureDataStructure architecture, FeatureStory featureStory) throws InvalidStoryException {
        this.title = featureStory.getTitle();
        this.tdds = getTdds(au, architecture, featureStory);
        this.functionalRequirements = getFunctionalRequirements(au, featureStory);
    }

    // TODO [TESTING]: unit test, especially that exceptions are thrown
    private List<JiraFunctionalRequirement> getFunctionalRequirements(ArchitectureUpdate au, FeatureStory featureStory) {
        return featureStory
                .getRequirementReferences()
                .stream()
                .map(reqId -> new JiraFunctionalRequirement(reqId, au.getFunctionalRequirements().get(reqId)))
                .collect(Collectors.toList());
    }

    // TODO [TESTING]: unit test, especially that exceptions are thrown
    private List<JiraTdd> getTdds(
            ArchitectureUpdate au, 
            ArchitectureDataStructure architecture,
            FeatureStory featureStory
    ) throws InvalidStoryException {

        List<JiraTdd> tdds = new ArrayList<>();
        for(var tddId : featureStory.getTddReferences()){
            var tdd = au.getTDDs()
                .entrySet()
                .stream()
                .filter(componentEntry -> componentEntry.getValue().containsKey(tddId))
                .map(componentEntry -> new JiraTdd(
                            tddId,
                            componentEntry.getValue().get(tddId),
                            architecture.getModel().findEntityById(componentEntry.getKey().toString()).getPath().getPath()
                            ))
                .findAny()
                .orElseThrow(() -> new InvalidStoryException());
            tdds.add(tdd);
        }

        return tdds;
    }

    @ToString
    @EqualsAndHashCode
    @RequiredArgsConstructor
    public static class JiraTdd {
        private final Tdd.Id id;
        private final Tdd tdd;
        private final String component;

        public String getId() {
            return id.toString();
        }

        public String getComponent() {
            return component;
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

    public static class InvalidStoryException extends Exception {}
}

