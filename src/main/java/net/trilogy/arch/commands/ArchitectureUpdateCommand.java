package net.trilogy.arch.commands;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.File;

@Command(name = "architecture-update", aliases = {"au"})
public class ArchitectureUpdateCommand {
    private static final Log logger = LogFactory.getLog(ArchitectureUpdateCommand.class);
    public static final String ARCHITECTURE_UPDATES_ROOT_FOLDER = "architecture-updates";

    @Command(name = "initialize", aliases = {"init"})
    int initialize(
            @Parameters(index = "0", description = "Product documentation root directory") File productDocumentationRoot
    ) {
        File auFolder = productDocumentationRoot.toPath().resolve(ARCHITECTURE_UPDATES_ROOT_FOLDER).toFile();
        boolean succeeded = auFolder.mkdir();
        if (!succeeded) {
            logger.error(String.format("Unable to create %s", auFolder.getAbsolutePath()));
            return 1;
        }
        logger.info(String.format("Architecture updates initialized under - %s", auFolder.getAbsolutePath()));
        return 0;
    }
}
