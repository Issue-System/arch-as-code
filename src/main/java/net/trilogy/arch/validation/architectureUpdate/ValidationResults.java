package net.trilogy.arch.validation.architectureUpdate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ValidationResults {
    private final LinkedHashSet<ValidationError> errors;

    public ValidationResults(Collection<ValidationError> errors) {
        this.errors = new LinkedHashSet<>(errors);
    }

    public boolean isValid() {
        return errors.isEmpty();
    }
    public boolean isValid(ValidationStage stage) {
        return errors.stream().noneMatch(error -> error.getValidationErrorType().getStage() == stage);
    }

    public List<ValidationError> getErrors() {
        return new ArrayList<>(errors);
    }

    public List<ValidationError> getErrors(ValidationErrorType validationErrorType) {
        return errors.stream().filter(error -> error.getValidationErrorType() == validationErrorType).collect(Collectors.toList());
    }

    public Set<ValidationErrorType> getErrorTypesEncountered() {
        return errors.stream().map(ValidationError::getValidationErrorType).collect(Collectors.toSet());
    }
}
