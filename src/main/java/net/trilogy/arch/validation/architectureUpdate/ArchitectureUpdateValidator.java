package net.trilogy.arch.validation.architectureUpdate;

import io.vavr.collection.Stream;
import net.trilogy.arch.domain.architectureUpdate.ArchitectureUpdate;
import net.trilogy.arch.domain.architectureUpdate.Decision;
import net.trilogy.arch.domain.architectureUpdate.Tdd;

import java.util.Collection;
import java.util.HashSet;
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
                getBrokenTddReferenceErrors()
        ).collect(Collectors.toSet()));
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
        Set<Tdd.Id> allTddIds = getAllTddIds(au);
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

    private static Set<Tdd.Id> getAllTddIds(ArchitectureUpdate au) {
        return au.getTDDs().values().stream().flatMap(Collection::stream).map(Tdd::getId).collect(Collectors.toSet());
    }

    public static class Results {
        private final Set<ValidationError> errors;

        private Results(Set<ValidationError> errors) {
            this.errors = errors;
        }

        public boolean isValid() {
            return errors.isEmpty();
        }

        public Set<ValidationError> getErrors() {
            return new HashSet<>(errors);
        }

        public Set<ValidationError> getErrors(ErrorType errorType) {
            return errors.stream().filter(error -> error.getErrorType() == errorType).collect(Collectors.toSet());
        }

        public Set<ErrorType> getErrorTypesEncountered() {
            return errors.stream().map(ValidationError::getErrorType).collect(Collectors.toSet());
        }
    }

    public static class ValidationError {
        private final ErrorType errorType;
        private final Decision.Id element;
        private final String description;

        private static ValidationError forMissingTddReference(Decision.Id entityId) {
            return new ValidationError(
                    ErrorType.decisions_must_have_at_least_one_tdd,
                    entityId,
                    String.format("Decision \"%s\" must have at least one TDD reference.", entityId.getId())
            );
        }

        private static ValidationError forInvalidTddReference(Decision.Id entityId, Tdd.Id tddId) {
            return new ValidationError(
                    ErrorType.invalid_tdd_reference,
                    entityId,
                    String.format("Decision \"%s\" contains invalid TDD reference \"%s\".", entityId.getId(), tddId.getId())
            );
        }

        private ValidationError(ErrorType errorType, Decision.Id element, String description) {
            this.errorType = errorType;
            this.element = element;
            this.description = description;
        }

        public Decision.Id getElement() {
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
        decisions_must_have_at_least_one_tdd,
        invalid_tdd_reference
    }
}
