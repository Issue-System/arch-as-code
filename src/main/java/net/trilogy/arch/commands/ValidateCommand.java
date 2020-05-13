package net.trilogy.arch.commands;

import net.trilogy.arch.validation.ArchitectureDataStructureValidatorFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

import com.google.common.annotations.VisibleForTesting;

@CommandLine.Command(name = "validate", mixinStandardHelpOptions = true, description = "Validate a product's architecture")
public class ValidateCommand implements Callable<Integer> {
    private static final Log logger = LogFactory.getLog(ValidateCommand.class);

    @CommandLine.Parameters(index = "0", paramLabel = "PRODUCT_DOCUMENTATION_PATH", description = "Product documentation root where data-structure.yml is located.")
    File productDocumentationRoot;
    private final String manifestFileName;

    @VisibleForTesting
    public ValidateCommand(File productDocumentationRoot, String manifestFileName) {
        this.productDocumentationRoot = productDocumentationRoot;
        this.manifestFileName = manifestFileName;
    }

    public ValidateCommand() {
        this.manifestFileName = "data-structure.yml";
    }

    @Override
    // TODO [TESTING]: add sad path coverage e2e tests
    public Integer call() throws IOException {
        List<String> messageSet = ArchitectureDataStructureValidatorFactory.create().validate(productDocumentationRoot, this.manifestFileName);

        if (messageSet.isEmpty()) {
            logger.info(manifestFileName + " is valid.");
        } else {
            logger.error(String.format("%s is invalid. (%d)", manifestFileName, messageSet.size()));
            messageSet.forEach(logger::error);
        }

        return messageSet.size();
    }
}
