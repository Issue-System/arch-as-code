package net.trilogy.arch.commands.architectureUpdate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.util.concurrent.Callable;

@Command(name = "initialize", aliases = "init", description = "Initialize the architecture updates work space.")
public class AuInitializeCommand implements Callable<Integer> {
    private static final Log logger = LogFactory.getLog(AuInitializeCommand.class);

    @Parameters(index = "0", description = "Product documentation root directory")
    private File productDocumentationRoot;

    @Override
    public Integer call() {
        File auFolder = Helpers.getAuFolder(productDocumentationRoot);
        boolean succeeded = auFolder.mkdir();
        if (!succeeded) {
            logger.error(String.format("Unable to create %s", auFolder.getAbsolutePath()));
            return 1;
        }
        logger.info(String.format("Architecture updates initialized under - %s", auFolder.getAbsolutePath()));
        return 0;
    }
}
