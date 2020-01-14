package net.nahknarmi.arch.commands;

import com.networknt.schema.ValidationMessage;
import net.nahknarmi.arch.schema.ArchitectureDataStructureSchemaValidator;
import picocli.CommandLine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Set;
import java.util.concurrent.Callable;

import static com.google.common.base.Preconditions.checkArgument;

@CommandLine.Command(name = "validate", description = "Validate product architecture yaml")
public class ValidateCommand implements Callable<Integer> {
    @CommandLine.Parameters(index = "0", paramLabel = "PRODUCT_DOCUMENTATION_PATH", description = "Product documentation root where data-structure.yml is located.")
    File productDocumentationRoot;
    private final String manifestFileName;

    // Only for testing purposes
    public ValidateCommand(File productDocumentationRoot, String manifestFileName) {
        this.productDocumentationRoot = productDocumentationRoot;
        this.manifestFileName = manifestFileName;
    }

    // For production
    public ValidateCommand() {
        this.manifestFileName = "data-structure.yml";
    }


    @Override
    public Integer call() throws FileNotFoundException {
        File manifestFile = new File(productDocumentationRoot.getAbsolutePath() + File.separator + manifestFileName);
        checkArgument(manifestFile.exists(), "Product Architecture data-structure.yml file does not exist.");

        Set<ValidationMessage> messageSet = new ArchitectureDataStructureSchemaValidator().validate(new FileInputStream(manifestFile));

        if (messageSet.isEmpty()) {
            System.out.println(manifestFileName + " is valid.");
        } else {
            System.out.println(manifestFileName + " is invalid.");
            messageSet.forEach(x -> System.err.println(x.getMessage()));
            return 1;
        }

        return 0;
    }
}
