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
import net.trilogy.arch.domain.c4.C4Path;

import java.util.ArrayList;
import java.util.List;

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

    private List<JiraFunctionalRequirement> getFunctionalRequirements(ArchitectureUpdate au, FeatureStory featureStory) throws InvalidStoryException {
        final var requirements = new ArrayList<JiraFunctionalRequirement>();
        for (var reqId : featureStory.getRequirementReferences()) {
            if (!au.getFunctionalRequirements().containsKey(reqId)) throw new InvalidStoryException();
            requirements.add(new JiraFunctionalRequirement(reqId, au.getFunctionalRequirements().get(reqId)));
        }
        return requirements;
    }

    private List<JiraTdd> getTdds(
            ArchitectureUpdate au,
            ArchitectureDataStructure architecture,
            FeatureStory featureStory
    ) throws InvalidStoryException {

        List<JiraTdd> tdds = new ArrayList<>();
        for (var tddId : featureStory.getTddReferences()) {
            var tdd = au.getTddContainersByComponent()
                    .stream()
                    .filter(container -> container.getTdds().containsKey(tddId))
                    .filter(container -> fetchPath(architecture, container.getComponentId()) != null)
                    .map(container -> new JiraTdd(
                            tddId,
                            container.getTdds().get(tddId),
                            fetchPath(architecture, container.getComponentId()).getPath()
                    ))
                    .findAny()
                    .orElseThrow(InvalidStoryException::new);
            tdds.add(tdd);
        }

        return tdds;
    }

    private C4Path fetchPath(ArchitectureDataStructure architecture, Tdd.ComponentReference componentReference) {
        C4Path path;
        try {
            path = architecture.getModel().findEntityById(componentReference.toString()).getPath();
        } catch ( Exception e) {
            path = null;
        }

        return path;
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

    public static class InvalidStoryException extends Exception {
    }
}

