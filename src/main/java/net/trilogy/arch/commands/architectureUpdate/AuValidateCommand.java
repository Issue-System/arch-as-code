package net.trilogy.arch.commands.architectureUpdate;

import net.trilogy.arch.adapter.ArchitectureUpdateObjectMapper;
import net.trilogy.arch.domain.architectureUpdate.ArchitectureUpdate;
import net.trilogy.arch.validation.architectureUpdate.ArchitectureUpdateValidator;
import net.trilogy.arch.validation.architectureUpdate.ValidationError;
import net.trilogy.arch.validation.architectureUpdate.ValidationStage;
import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
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
    public Integer call() throws IOException {
        ArchitectureUpdate au = getAu();
        var validationResults = ArchitectureUpdateValidator.validate(au);

        List<ValidationStage> stages = determineValidationStage(tddValidation, capabilityValidation);

        boolean isValid = stages.stream().map(validationResults::isValid).noneMatch(result -> result == false);

        if (!isValid) {
            spec.commandLine().getErr().println(
                    stages.stream()
                            .map(validationResults::getErrors)
                            .flatMap(Collection::stream)
                            .map(ValidationError::getDescription)
                            .collect(Collectors.joining())
            );
            return 1;
        } else {
            spec.commandLine().getOut().println("Success, no errors found.");
        }

        return 0;
    }

    private List<ValidationStage> determineValidationStage(boolean tddValidation, boolean capabilityValidation) {
        if (tddValidation && capabilityValidation) return List.of(ValidationStage.TDD, ValidationStage.CAPABILITY);
        if (tddValidation) return List.of(ValidationStage.TDD);
        if (capabilityValidation) return List.of(ValidationStage.CAPABILITY);
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
