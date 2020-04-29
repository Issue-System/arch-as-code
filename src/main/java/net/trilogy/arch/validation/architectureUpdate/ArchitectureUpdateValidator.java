package net.trilogy.arch.validation.architectureUpdate;

import io.vavr.collection.Stream;
import net.trilogy.arch.domain.ArchitectureDataStructure;
import net.trilogy.arch.domain.architectureUpdate.ArchitectureUpdate;
import net.trilogy.arch.domain.architectureUpdate.FunctionalRequirement;
import net.trilogy.arch.domain.architectureUpdate.Tdd;
import net.trilogy.arch.domain.c4.BaseEntity;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ArchitectureUpdateValidator {

    private final ArchitectureUpdate au;
    private final ArchitectureDataStructure architecture;

    private final Set<String> allComponentIdsInArchitecture;

    private final Set<Tdd.Id> allTddIdsInStories;
    private final List<Tdd.Id> allTddIds;
    private final Set<FunctionalRequirement.Id> allFunctionalRequirementIds;
    private final Set<Tdd.Id> allTddIdsInDecisions;
    private final Set<Tdd.Id> allTddIdsInFunctionalRequirements;

    public static ValidationResult validate(ArchitectureUpdate au, ArchitectureDataStructure architecture) {
        return new ArchitectureUpdateValidator(au, architecture).run();
    }

    private ArchitectureUpdateValidator(ArchitectureUpdate au, ArchitectureDataStructure architecture) {
        this.au = au;
        this.architecture = architecture;

        allComponentIdsInArchitecture = getAllComponentIdsInArchitecture();

        allTddIdsInStories = getAllTddIdsReferencedByStories();
        allTddIds = getAllTddIds();
        allTddIdsInDecisions = getAllTddIdsReferencedByDecisions();
        allTddIdsInFunctionalRequirements = getAllTddIdsReferencedByFunctionalRequirements();
        allFunctionalRequirementIds = getAllFunctionalRequirementIds();
    }

    private ValidationResult run() {
        return new ValidationResult(Stream.concat(

                // Decisions must have >=1 valid TDD
                getErrors_DecisionsMustHaveTdds(),
                getErrors_DecisionsTddsMustBeValidReferences(),

                // TDDs must refer to valid components
                getErrors_TddsComponentsMustBeValidReferences(),

                // TDDs must be referred to by >= 1 decision or requirement (no orphan TDDs)
                getErrors_TddsMustHaveDecisionsOrRequirements(),

                // Stories must refer to >= 1 valid functional requirements
                getErrors_StoriesMustHaveFunctionalRequirements(),
                getErrors_StoriesFunctionalRequirementsMustBeValidReferences(),

                // Stories must refer to >=1 valid TDDs
                getErrors_StoriesMustHaveTdds(),
                getErrors_StoriesTddsMustBeValidReferences(),

                // All TDDs must have >=1 story
                getErrors_TddsMustHaveStories(),

                // All functional requirements have >=1 story
                getErrors_FunctionalRequirementsMustHaveStories(),

                // If Functional Requirements have TDDs, they must be valid
                getErrors_FunctionalRequirementsTddsMustBeValidReferences(),

                // TDDs in different components must not share the same id
                getErrors_TddsMustHaveUniqueIds()

        ).collect(Collectors.toList()));
    }

    private Set<ValidationError> getErrors_TddsMustHaveUniqueIds() {
        return findDuplicates(allTddIds).stream()
                .map(ValidationError::forDuplicatedTdd)
                .collect(Collectors.toSet());
    }

    private Set<ValidationError> getErrors_StoriesFunctionalRequirementsMustBeValidReferences() {
        return au.getCapabilityContainer().getFeatureStories()
                .stream()
                .filter(story -> story.getRequirementReferences() != null)
                .flatMap(story ->
                        story.getRequirementReferences()
                                .stream()
                                .filter(funcReq -> !allFunctionalRequirementIds.contains(funcReq))
                                .map(funcReq -> ValidationError.forFunctionalRequirementsMustBeValidReferences(story.getTitle(), funcReq))
                )
                .collect(Collectors.toSet());
    }

    private Set<ValidationError> getErrors_FunctionalRequirementsMustHaveStories() {
        var storyReferencedFunctionalRequirements = getAllFunctionalRequirementsReferencedByStories();
        return au.getFunctionalRequirements().entrySet().stream()
                .filter(funcReqEntry -> !storyReferencedFunctionalRequirements.contains(funcReqEntry.getKey()))
                .map(funcReqEntry -> ValidationError.forMustHaveStories(funcReqEntry.getKey()))
                .collect(Collectors.toSet());
    }

    private Set<ValidationError> getErrors_TddsComponentsMustBeValidReferences() {
        return au.getTDDs()
                .keySet()
                .stream()
                .filter(componentReference -> !allComponentIdsInArchitecture.contains(componentReference.getId()))
                .map(ValidationError::forTddsComponentsMustBeValidReferences)
                .collect(Collectors.toSet());
    }

    private Set<ValidationError> getErrors_TddsMustHaveDecisionsOrRequirements() {
        return allTddIds.stream()
                .filter(tddId -> !allTddIdsInFunctionalRequirements.contains(tddId))
                .filter(tddId -> !allTddIdsInDecisions.contains(tddId))
                .map(ValidationError::forTddsMustHaveDecisionsOrRequirements)
                .collect(Collectors.toSet());
    }

    private Set<ValidationError> getErrors_TddsMustHaveStories() {
        return allTddIds.stream()
                .filter(tdd -> !allTddIdsInStories.contains(tdd))
                .map(ValidationError::forMustHaveStories)
                .collect(Collectors.toSet());
    }

    private Set<ValidationError> getErrors_DecisionsMustHaveTdds() {
        return au.getDecisions()
                .entrySet()
                .stream()
                .filter(decisionEntry -> decisionEntry.getValue().getTddReferences() == null || decisionEntry.getValue().getTddReferences().isEmpty())
                .map(decisionEntry -> ValidationError.forDecisionsMustHaveTdds(decisionEntry.getKey()))
                .collect(Collectors.toSet());
    }

    private Set<ValidationError> getErrors_StoriesMustHaveTdds() {
        return au.getCapabilityContainer().getFeatureStories()
                .stream()
                .filter(story -> story.getTddReferences() == null || story.getTddReferences().isEmpty())
                .map(story -> ValidationError.forStoriesMustHaveTdds(story.getTitle()))
                .collect(Collectors.toSet());
    }

    private Set<ValidationError> getErrors_StoriesMustHaveFunctionalRequirements() {
        return au.getCapabilityContainer().getFeatureStories()
                .stream()
                .filter(story -> story.getRequirementReferences() == null || story.getRequirementReferences().isEmpty())
                .map(story -> ValidationError.forStoriesMustHaveFunctionalRequirements(story.getTitle()))
                .collect(Collectors.toSet());
    }

    private Set<ValidationError> getErrors_DecisionsTddsMustBeValidReferences() {
        return au.getDecisions()
                .entrySet()
                .stream()
                .filter(decisionEntry -> decisionEntry.getValue().getTddReferences() != null)
                .flatMap(decisionEntry ->
                        decisionEntry.getValue().getTddReferences()
                                .stream()
                                .filter(tdd -> !allTddIds.contains(tdd))
                                .map(tdd -> ValidationError.forTddsMustBeValidReferences(decisionEntry.getKey(), tdd))
                )
                .collect(Collectors.toSet());
    }

    private Set<ValidationError> getErrors_StoriesTddsMustBeValidReferences() {
        return au.getCapabilityContainer()
                .getFeatureStories()
                .stream()
                .filter(story -> story.getTddReferences() != null)
                .flatMap(story ->
                        story.getTddReferences()
                                .stream()
                                .filter(tdd -> !allTddIds.contains(tdd))
                                .map(tdd -> ValidationError.forStoriesTddsMustBeValidReferences(tdd, story.getTitle()))
                )
                .collect(Collectors.toSet());
    }

    private Set<ValidationError> getErrors_FunctionalRequirementsTddsMustBeValidReferences() {
        return au.getFunctionalRequirements()
                .entrySet()
                .stream()
                .filter(functionalEntry -> functionalEntry.getValue().getTddReferences() != null)
                .flatMap(functionalEntry ->
                        functionalEntry.getValue().getTddReferences()
                                .stream()
                                .filter(tdd -> !allTddIds.contains(tdd))
                                .map(tdd -> ValidationError.forTddsMustBeValidReferences(functionalEntry.getKey(), tdd)))
                .collect(Collectors.toSet());
    }

    private List<Tdd.Id> getAllTddIds() {
        return au.getTDDs()
                .values()
                .stream()
                .flatMap(map -> map.keySet().stream())
                .collect(Collectors.toList());
    }

    private Set<FunctionalRequirement.Id> getAllFunctionalRequirementIds() {
        return au.getFunctionalRequirements().keySet();
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

    private Set<FunctionalRequirement.Id> getAllFunctionalRequirementsReferencedByStories() {
        return au.getCapabilityContainer()
                .getFeatureStories().stream()
                .flatMap(story -> story.getRequirementReferences().stream())
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

    private Set<String> getAllComponentIdsInArchitecture() {
        return this.architecture.getModel().getComponents().stream().map(BaseEntity::getId).collect(Collectors.toSet());
    }

    private <T> Set<T> findDuplicates(Collection<T> collection) {
        Set<T> uniques = new HashSet<>();
        return collection
                .stream()
                .filter(t -> !uniques.add(t))
                .collect(Collectors.toSet());
    }
}
