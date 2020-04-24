package net.trilogy.arch.commands.architectureUpdate;

import net.trilogy.arch.adapter.ArchitectureUpdateObjectMapper;
import net.trilogy.arch.domain.architectureUpdate.ArchitectureUpdate;
import net.trilogy.arch.validation.architectureUpdate.ArchitectureUpdateValidator;
import net.trilogy.arch.validation.architectureUpdate.ValidationError;
import net.trilogy.arch.validation.architectureUpdate.ValidationResult;
import net.trilogy.arch.validation.architectureUpdate.ValidationStage;
import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.Parameters;
import static picocli.CommandLine.Spec;

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
    public Integer call() throws IOException {
        ArchitectureUpdate au = getAu();
        var validationResults = ArchitectureUpdateValidator.validate(au);

        List<ValidationStage> stages = determineValidationStages(tddValidation, capabilityValidation);
        boolean areAllStagesValid = stages.stream().allMatch(validationResults::isValid);

        if (!areAllStagesValid) {
            String resultToDisplay = getPrettyErrorsString(stages, validationResults);
            spec.commandLine().getErr().println(resultToDisplay);
            return 1;
        } else {
            spec.commandLine().getOut().println("Success, no errors found.");
        }

        return 0;
    }

    private String getPrettyErrorsString(List<ValidationStage> stages, ValidationResult validationResults) {
        return stages.stream()
                .map(validationResults::getErrors)
                .filter(errors -> !errors.isEmpty())
                .map(this::getPrettyErrorStringByType)
                .collect(Collectors.joining())
                .trim();
    }

    private String getPrettyErrorStringByType(List<ValidationError> errors) {
        return errors.get(0).getValidationErrorType() + ":\n    " + errors.stream().map(error -> error.getDescription() + "\n").collect(Collectors.joining());
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

    private ArchitectureUpdate getAu() throws IOException {
        Path auPath = productDocumentationRoot.toPath()
                .resolve("architecture-updates")
                .resolve(architectureUpdateFileName)
                .toAbsolutePath();
        return new ArchitectureUpdateObjectMapper().readValue(Files.readString(auPath));
    }
}
