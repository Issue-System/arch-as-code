package net.trilogy.arch.commands.architectureUpdate.view;

import net.trilogy.arch.validation.architectureUpdate.ArchitectureUpdateValidator.ErrorType;
import net.trilogy.arch.validation.architectureUpdate.ArchitectureUpdateValidator.Results;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class AuValidateErrorPresenter {
    static public String present(PresentationMode mode, Results validationResults) {
        return Arrays.stream(ErrorType.values())
                .map(validationResults::getErrors)
                .filter(it -> !it.isEmpty())
                .map(ArrayList::new)
                .map(errors -> errors.get(0).getErrorType() + ":" + errors.stream().map(error -> "\n    " + error.getDescription()).collect(Collectors.joining()) + "\n")
                .collect(Collectors.joining());
    }

    public enum PresentationMode {
        TDD_VALIDATION,
        CAPABILITIES_VALIDATION,
        FULL_VALIDATION
    }
}
