package net.trilogy.arch.validation.architectureUpdate;

import net.trilogy.arch.domain.architectureUpdate.ArchitectureUpdate;
import net.trilogy.arch.domain.architectureUpdate.Decision;
import net.trilogy.arch.domain.architectureUpdate.Tdd;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ArchitectureUpdateValidator {
    public static Results validate(ArchitectureUpdate au) {
        final Results results = new Results();

        for (Map.Entry<Decision.Id, Decision> entry : au.getDecisions().entrySet()) {
            final Decision decisionBeingChecked = entry.getValue();
            final Decision.Id decisionIdBeingChecked = entry.getKey();
            if (decisionBeingChecked.getTddReferences().isEmpty()) {
                ValidationError error = new ValidationError(ErrorType.decisions_must_have_at_least_one_tdd, decisionIdBeingChecked, String.format("Decision \"%s\" must have at least one TDD reference.", decisionIdBeingChecked.getId()));
                results.add(error);
            } else {
                final Set<Tdd.Id> allTdds = getAllTddIds(au);
                decisionBeingChecked.getTddReferences().forEach(tdd_ref -> {
                    if (!allTdds.contains(tdd_ref)) {
                        ValidationError error = new ValidationError(ErrorType.invalid_tdd_reference, decisionIdBeingChecked, String.format("Decision \"%s\" contains invalid TDD reference \"%s\".", decisionIdBeingChecked.getId(), tdd_ref.getId()));
                        results.add(error);
                    }
                });
            }
        }

        return results;
    }

    private static Set<Tdd.Id> getAllTddIds(ArchitectureUpdate au) {
        return au.getTDDs().values().stream().flatMap(Collection::stream).map(Tdd::getId).collect(Collectors.toSet());
    }

    public static class Results {
        private final Set<ValidationError> errors;

        private Results() {
            errors = new LinkedHashSet<>();
        }

        public boolean isValid() {
            return errors.isEmpty();
        }

        public Set<ValidationError> getErrors() {
            return errors;
        }

        public Set<ValidationError> getErrors(ErrorType errorType) {
            return errors.stream().filter(error -> error.getErrorType() == errorType).collect(Collectors.toSet());
        }

        public Set<ErrorType> getErrorTypes() {
            return errors.stream().map(ValidationError::getErrorType).collect(Collectors.toSet());
        }

        private void add(ValidationError error) {
            errors.add(error);
        }
    }

    public static class ValidationError {
        private final ErrorType errorType;
        private final Decision.Id element;
        private final String description;

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
