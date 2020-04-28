package net.trilogy.arch.commands.architectureUpdate;

import net.trilogy.arch.adapter.ArchitectureUpdateObjectMapper;
import net.trilogy.arch.domain.architectureUpdate.ArchitectureUpdate;
import net.trilogy.arch.validation.architectureUpdate.*;
import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static picocli.CommandLine.*;

@Command(name = "validate", description = "Validate Architecture Update")
public class AuValidateCommand implements Callable<Integer> {
    @Parameters(index = "0", description = "File name of architecture update to validate")
    private String architectureUpdateFileName;

    @Parameters(index = "1", description = "Product documentation root directory")
    private File productDocumentationRoot;

    @CommandLine.Option(names = {"-t", "--TDDs"}, description = "Run validation for TDDs")
    boolean tddValidation;

    @CommandLine.Option(names = {"-c", "--capabilities"}, description = "Run validation for capabilities")
    boolean capabilityValidation;

    @Spec
    private CommandSpec spec;

    @Override
    public Integer call() {
        Path auPath = productDocumentationRoot.toPath()
                .resolve("architecture-updates")
                .resolve(architectureUpdateFileName)
                .toAbsolutePath();

        ValidationResult validationResults;
        // TODO FUTURE: Use JSON schema validation
        try {
            ArchitectureUpdate au = new ArchitectureUpdateObjectMapper().readValue(Files.readString(auPath));
            validationResults = ArchitectureUpdateValidator.validate(au);
        } catch (IOException | RuntimeException e) {
            spec.commandLine().getErr().println("Invalid structure.");
            return 1;
        }

        List<ValidationStage> stages = determineValidationStages(tddValidation, capabilityValidation);
        boolean areAllStagesValid = stages.stream().allMatch(validationResults::isValid);

        if (!areAllStagesValid) {
            List<ValidationError> errors = getErrorsOfStages(stages, validationResults);
            String resultToDisplay = getPrettyStringOfErrors(errors);
            spec.commandLine().getErr().println(resultToDisplay);
            return 1;
        } else {
            spec.commandLine().getOut().println("Success, no errors found.");
        }

        return 0;
    }

    private String getPrettyStringOfErrors(List<ValidationError> errors) {
        return getTypes(errors).stream()
                .map(type -> getErrorsOfType(type, errors))
                .map(this::getPrettyStringOfErrorsInSingleType)
                .collect(Collectors.joining())
                .trim();
    }

    private List<ValidationError> getErrorsOfStages(List<ValidationStage> stages, ValidationResult validationResults) {
        return stages.stream()
                .map(validationResults::getErrors)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private List<ValidationErrorType> getTypes(List<ValidationError> errors) {
        return errors.stream()
                .map(ValidationError::getValidationErrorType)
                .distinct()
                .collect(Collectors.toList());
    }

    private List<ValidationError> getErrorsOfType(ValidationErrorType type, List<ValidationError> allErrors) {
        return allErrors.stream()
                .filter(error -> error.getValidationErrorType() == type)
                .collect(Collectors.toList());
    }

    private String getPrettyStringOfErrorsInSingleType(List<ValidationError> errors) {
        return errors.get(0).getValidationErrorType() +
                ":" +
                errors.stream().map(error -> "\n    " + error.getDescription()).collect(Collectors.joining()) +
                "\n";
    }

    private List<ValidationStage> determineValidationStages(boolean tddValidation, boolean capabilityValidation) {
        if (tddValidation && capabilityValidation) {
            return List.of(ValidationStage.TDD, ValidationStage.CAPABILITY);
        }
        if (tddValidation) {
            return List.of(ValidationStage.TDD);
        }
        if (capabilityValidation) {
            return List.of(ValidationStage.CAPABILITY);
        }
        return List.of(ValidationStage.values());
    }

}
