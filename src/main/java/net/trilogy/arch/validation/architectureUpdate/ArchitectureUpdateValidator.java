package net.trilogy.arch.validation.architectureUpdate;

import io.vavr.collection.Stream;
import net.trilogy.arch.domain.architectureUpdate.ArchitectureUpdate;
import net.trilogy.arch.domain.architectureUpdate.Tdd;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class ArchitectureUpdateValidator {
    private final ArchitectureUpdate au;

    private final Set<Tdd.Id> allTddIdsInStories;
    private final Set<Tdd.Id> allTddIds;
    private final Set<Tdd.Id> allTddIdsInDecisions;
    private final Set<Tdd.Id> allTddIdsInFunctionalRequirements;

    public static ValidationResult validate(ArchitectureUpdate au) {
        return new ArchitectureUpdateValidator(au).run();
    }

    private ArchitectureUpdateValidator(ArchitectureUpdate au) {
        this.au = au;
        allTddIdsInStories = getAllTddIdsReferencedByStories();
        allTddIds = getAllTddIds();
        allTddIdsInDecisions = getAllTddIdsReferencedByDecisions();
        allTddIdsInFunctionalRequirements = getAllTddIdsReferencedByFunctionalRequirements();
    }

    private ValidationResult run() {
        return new ValidationResult(Stream.concat(
                getMissingTddReferenceErrors(),
                getBrokenDecisionsTddReferenceErrors(),
                getBrokenFunctionalRequirementsTddReferenceErrors(),
                getTDDsWithoutStoriesErrors(),
                getTDDsWithoutCauseErrors()
        ).collect(Collectors.toList()));
    }

    private Set<ValidationError> getTDDsWithoutCauseErrors() {
        return allTddIds.stream()
                .filter(tddId -> !allTddIdsInFunctionalRequirements.contains(tddId))
                .filter(tddId -> !allTddIdsInDecisions.contains(tddId))
                .map(ValidationError::forTddWithoutCause)
                .collect(Collectors.toSet());
    }

    private Set<ValidationError> getTDDsWithoutStoriesErrors() {
        return getAllTddIds().stream()
                .filter(tdd -> !allTddIdsInStories.contains(tdd))
                .map(ValidationError::forTddWithoutStory)
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

    private Set<ValidationError> getBrokenDecisionsTddReferenceErrors() {
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

    private Set<ValidationError> getBrokenFunctionalRequirementsTddReferenceErrors() {
        return au.getFunctionalRequirements()
                .entrySet()
                .stream()
                .filter(functionalEntry -> functionalEntry.getValue().getTddReferences() != null)
                .flatMap(functionalEntry ->
                        functionalEntry.getValue().getTddReferences()
                                .stream()
                                .filter(tdd -> !allTddIds.contains(tdd))
                                .map(tdd -> ValidationError.forInvalidTddReference(functionalEntry.getKey(), tdd)))
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

    private Set<Tdd.Id> getAllTddIdsReferencedByFunctionalRequirements() {
        return au.getFunctionalRequirements()
                .values()
                .stream()
                .filter(requirement -> requirement.getTddReferences() != null)
                .flatMap(requirement -> requirement.getTddReferences().stream())
                .collect(Collectors.toSet());
    }

    private Set<Tdd.Id> getAllTddIdsReferencedByDecisions() {
        return au.getDecisions()
                .values()
                .stream()
                .filter(decision -> decision.getTddReferences() != null)
                .flatMap(decision -> decision.getTddReferences().stream())
                .collect(Collectors.toSet());
    }
}
