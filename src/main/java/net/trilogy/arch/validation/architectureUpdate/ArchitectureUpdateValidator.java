package net.trilogy.arch.validation.architectureUpdate;

import io.vavr.collection.Stream;
import net.trilogy.arch.domain.architectureUpdate.ArchitectureUpdate;
import net.trilogy.arch.domain.architectureUpdate.Tdd;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class ArchitectureUpdateValidator {
    private final ArchitectureUpdate au;

    public static ValidationResult validate(ArchitectureUpdate au) {
        return new ArchitectureUpdateValidator(au).run();
    }

    private ArchitectureUpdateValidator(ArchitectureUpdate au) {
        this.au = au;
    }

    private ValidationResult run() {
        return new ValidationResult(Stream.concat(
                getMissingTddReferenceErrors(),
                getBrokenTddReferenceErrors(),
                getTDDsWithoutStoriesErrors()
        ).collect(Collectors.toList()));
    }

    private Set<ValidationError> getTDDsWithoutStoriesErrors() {
        Set<Tdd.Id> allTddIdsInStories = getAllTddIdsReferencedByStories();
        return getAllTddIds().stream()
                .filter(tdd -> !allTddIdsInStories.contains(tdd))
                .map(ValidationError::forTddsWithoutStories)
                .collect(Collectors.toSet());
    }

    private Set<ValidationError> getMissingTddReferenceErrors() {
        return au.getDecisions()
                .entrySet()
                .stream()
                .filter(decisionEntry -> decisionEntry.getValue().getTddReferences() == null || decisionEntry.getValue().getTddReferences().isEmpty())
                .map(decisionEntry -> ValidationError.forMissingTddReference(decisionEntry.getKey()))
                .collect(Collectors.toSet());
    }

    private Set<ValidationError> getBrokenTddReferenceErrors() {
        Set<Tdd.Id> allTddIds = getAllTddIds();
        return au.getDecisions()
                .entrySet()
                .stream()
                .filter(decisionEntry -> decisionEntry.getValue().getTddReferences() != null)
                .flatMap(decisionEntry ->
                        decisionEntry.getValue().getTddReferences()
                                .stream()
                                .filter(tdd -> !allTddIds.contains(tdd))
                                .map(tdd -> ValidationError.forInvalidTddReference(decisionEntry.getKey(), tdd)))
                .collect(Collectors.toSet());
    }

    private Set<Tdd.Id> getAllTddIds() {
        return au.getTDDs()
                .values()
                .stream()
                .flatMap(Collection::stream)
                .map(Tdd::getId)
                .collect(Collectors.toSet());
    }

    private Set<Tdd.Id> getAllTddIdsReferencedByStories() {
        return au.getCapabilityContainer()
                .getFeatureStories()
                .stream()
                .flatMap(story -> story.getTddReferences().stream())
                .collect(Collectors.toSet());
    }
}
