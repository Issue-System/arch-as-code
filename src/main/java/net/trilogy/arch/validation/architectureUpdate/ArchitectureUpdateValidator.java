package net.trilogy.arch.validation.architectureUpdate;

import io.vavr.collection.Stream;
import net.trilogy.arch.domain.architectureUpdate.ArchitectureUpdate;
import net.trilogy.arch.domain.architectureUpdate.Decision;
import net.trilogy.arch.domain.architectureUpdate.EntityReference;
import net.trilogy.arch.domain.architectureUpdate.Tdd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ArchitectureUpdateValidator {
    private final ArchitectureUpdate au;

    public static Results validate(ArchitectureUpdate au) {
        return new ArchitectureUpdateValidator(au).run();
    }

    private ArchitectureUpdateValidator(ArchitectureUpdate au) {
        this.au = au;
    }

    private Results run() {
        return new Results(Stream.concat(
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


    public static class Results {
        private final LinkedHashSet<ValidationError> errors;

        public Results(Collection<ValidationError> errors) {
            this.errors = new LinkedHashSet<>(errors);
        }

        public boolean isValid() {
            return errors.isEmpty();
        }

        public List<ValidationError> getErrors() {
            return new ArrayList<>(errors);
        }

        public List<ValidationError> getErrors(ErrorType errorType) {
            return errors.stream().filter(error -> error.getErrorType() == errorType).collect(Collectors.toList());
        }

        public Set<ErrorType> getErrorTypesEncountered() {
            return errors.stream().map(ValidationError::getErrorType).collect(Collectors.toSet());
        }
    }

    public static class ValidationError {
        private final ErrorType errorType;
        private final EntityReference element;
        private final String description;

        public static ValidationError forMissingTddReference(Decision.Id entityId) {
            return new ValidationError(
                    ErrorType.missing_tdd,
                    entityId,
                    String.format("Decision \"%s\" must have at least one TDD reference.", entityId.getId())
            );
        }

        public static ValidationError forInvalidTddReference(Decision.Id entityId, Tdd.Id tddId) {
            return new ValidationError(
                    ErrorType.invalid_tdd_reference,
                    entityId,
                    String.format("Decision \"%s\" contains invalid TDD reference \"%s\".", entityId.getId(), tddId.getId())
            );
        }

        public static ValidationError forTddsWithoutStories(Tdd.Id entityId) {
            return new ValidationError(
                    ErrorType.missing_capability,
                    entityId,
                    String.format("TDD \"%s\" is not referred to by a story.", entityId.getId())
            );
        }

        private ValidationError(ErrorType errorType, EntityReference element, String description) {
            this.errorType = errorType;
            this.element = element;
            this.description = description;
        }

        public EntityReference getElement() {
            return element;
        }

        public String getDescription() {
            return description;
        }

        public ErrorType getErrorType() {
            return errorType;
        }
    }

    public enum ErrorType {
        missing_tdd("Missing TDD"),
        missing_capability("Missing Capability"),
        invalid_tdd_reference("Invalid TDD Reference");

        private final String label;

        ErrorType(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}
