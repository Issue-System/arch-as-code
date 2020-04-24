package net.trilogy.arch.commands.architectureUpdate;

import net.trilogy.arch.adapter.ArchitectureUpdateObjectMapper;
import net.trilogy.arch.domain.architectureUpdate.ArchitectureUpdate;
import net.trilogy.arch.validation.architectureUpdate.ArchitectureUpdateValidator;
import net.trilogy.arch.validation.architectureUpdate.ValidationStage;
import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.Callable;

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

        Optional<ValidationStage> stage = determineValidationStage(tddValidation, capabilityValidation);

        validationResults.getErrors();

        spec.commandLine().getOut().println("Success, no errors found.");
        return 0;
    }

    private Optional<ValidationStage> determineValidationStage(boolean tddValidation, boolean capabilityValidation) {
        if(tddValidation && capabilityValidation) return Optional.empty();
        else if(!tddValidation && !capabilityValidation) return Optional.empty();
        else if(tddValidation) return Optional.of(ValidationStage.TDD);
        return Optional.of(ValidationStage.CAPABILITY);
    }

    private ArchitectureUpdate getAu() throws IOException {
        Path auPath = productDocumentationRoot.toPath()
                .resolve("architecture-updates")
                .resolve(architectureUpdateFileName)
                .toAbsolutePath();
        return new ArchitectureUpdateObjectMapper().readValue(Files.readString(auPath));
    }
}
