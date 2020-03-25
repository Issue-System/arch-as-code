package net.trilogy.arch.commands;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.util.concurrent.Callable;

@Command(name = "architecture-update", aliases = {"au"}, description = "Architecture Update Commands", subcommands = {ArchitectureUpdateCommand.Initialize.class})
public class ArchitectureUpdateCommand implements Callable<Integer> {
    private static final Log logger = LogFactory.getLog(ArchitectureUpdateCommand.class);
    public static final String ARCHITECTURE_UPDATES_ROOT_FOLDER = "architecture-updates";

    @CommandLine.Spec
    CommandSpec spec;

    @Override
    public Integer call() {
        logger.info(spec.commandLine().getUsageMessage());
        return 0;
    }

    @Command(name = "initialize", aliases = {"init"}, description = "Initialize the Architecture Updates work space.")
    public static class Initialize implements Callable<Integer> {

        @Parameters(index = "0", description = "Product documentation root directory")
        File productDocumentationRoot;

        @Override
        public Integer call() {
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
}
