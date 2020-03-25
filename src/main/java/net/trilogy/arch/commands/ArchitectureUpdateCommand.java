package net.trilogy.arch.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.File;

@Command(name = "architecture-update", aliases = {"au"})
public class ArchitectureUpdateCommand {
    public static final String ARCHITECTURE_UPDATES_ROOT_FOLDER = "architecture-updates";

    @Command(name = "initialize", aliases = {"init"})
    void initialize(
            @Parameters(index = "0", description = "Product documentation root directory") File productDocumentationRoot
    ) {
        boolean mkdir = productDocumentationRoot.toPath().resolve(ARCHITECTURE_UPDATES_ROOT_FOLDER).toFile().mkdir();
    }
}
