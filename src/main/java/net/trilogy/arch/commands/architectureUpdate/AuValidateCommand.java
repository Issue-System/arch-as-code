package net.trilogy.arch.commands.architectureUpdate;

import net.trilogy.arch.adapter.ArchitectureUpdateObjectMapper;
import net.trilogy.arch.domain.architectureUpdate.ArchitectureUpdate;
import net.trilogy.arch.validation.architectureUpdate.ArchitectureUpdateValidator;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "validate", description = "Validate Architecture Update")
public class AuValidateCommand implements Callable<Integer> {
    @CommandLine.Parameters(index = "0", description = "File name of architecture update to validate")
    private String architectureUpdateFileName;

    @CommandLine.Parameters(index = "1", description = "Product documentation root directory")
    private File productDocumentationRoot;

    @Override
    public Integer call() throws IOException {
        ArchitectureUpdate au = getAu();
        if (!ArchitectureUpdateValidator.isValid(au)) {
            return 1;
        }
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
