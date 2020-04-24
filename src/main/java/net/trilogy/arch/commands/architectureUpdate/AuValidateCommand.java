package net.trilogy.arch.commands.architectureUpdate;

import net.trilogy.arch.adapter.ArchitectureUpdateObjectMapper;
import net.trilogy.arch.domain.architectureUpdate.ArchitectureUpdate;
import net.trilogy.arch.validation.architectureUpdate.ArchitectureUpdateValidator;
import picocli.CommandLine.Model.CommandSpec;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

import static picocli.CommandLine.*;

@Command(name = "validate", description = "Validate Architecture Update")
public class AuValidateCommand implements Callable<Integer> {
    @Parameters(index = "0", description = "File name of architecture update to validate")
    private String architectureUpdateFileName;

    @Parameters(index = "1", description = "Product documentation root directory")
    private File productDocumentationRoot;

    @Spec
    private CommandSpec spec;


    @Override
    public Integer call() throws IOException {
        ArchitectureUpdate au = getAu();
        var validationResults = ArchitectureUpdateValidator.validate(au);
        if (!validationResults.isValid()) {
            spec.commandLine().getErr().println("Errors found!");
            validationResults.getErrors().forEach(error ->
                    spec.commandLine().getErr().println(error.getDescription())
            );
            return 1;
        }

        spec.commandLine().getOut().println("Success, no errors found.");
        return 0;
    }

    private ArchitectureUpdate getAu() throws IOException {
        Path auPath = productDocumentationRoot.toPath()
                .resolve("architecture-updates")
                .resolve(architectureUpdateFileName)
                .toAbsolutePath();
        return new ArchitectureUpdateObjectMapper().readValue(Files.readString(auPath));
    }
}
