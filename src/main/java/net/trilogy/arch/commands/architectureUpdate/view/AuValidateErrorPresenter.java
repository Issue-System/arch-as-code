package net.trilogy.arch.commands.architectureUpdate.view;

import net.trilogy.arch.validation.architectureUpdate.ArchitectureUpdateValidator.ErrorType;
import net.trilogy.arch.validation.architectureUpdate.ArchitectureUpdateValidator.Results;

import java.util.List;
import java.util.stream.Collectors;

import static net.trilogy.arch.commands.architectureUpdate.view.AuValidateErrorPresenter.PresentationMode.CAPABILITIES_VALIDATION;
import static net.trilogy.arch.commands.architectureUpdate.view.AuValidateErrorPresenter.PresentationMode.TDD_VALIDATION;

public class AuValidateErrorPresenter {
    static public String present(PresentationMode mode, Results validationResults) {
        return getMatchingTypes(mode)
                .stream()
                .map(validationResults::getErrors)
                .filter(it -> !it.isEmpty())
                .map(errors -> errors.get(0).getErrorType() + ":" + errors.stream().map(error -> "\n    " + error.getDescription()).collect(Collectors.joining()) + "\n")
                .collect(Collectors.joining());
    }

    private static List<ErrorType> getMatchingTypes(PresentationMode mode) {
        if (mode == CAPABILITIES_VALIDATION) {
            return List.of(ErrorType.missing_capability);
        } else if (mode == TDD_VALIDATION) {
            return List.of(ErrorType.missing_tdd, ErrorType.invalid_tdd_reference);
        }
        return List.of(ErrorType.values());
    }

    public enum PresentationMode {
        TDD_VALIDATION,
        CAPABILITIES_VALIDATION,
        FULL_VALIDATION
    }
}
