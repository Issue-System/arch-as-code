package net.trilogy.arch.commands;

import com.google.common.annotations.VisibleForTesting;
import lombok.Getter;
import net.trilogy.arch.commands.mixin.DisplaysErrorMixin;
import net.trilogy.arch.commands.mixin.DisplaysOutputMixin;
import net.trilogy.arch.validation.ArchitectureDataStructureValidatorFactory;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "validate", mixinStandardHelpOptions = true, description = "Validate a product's architecture")
public class ValidateCommand implements Callable<Integer>, DisplaysOutputMixin, DisplaysErrorMixin {

    @CommandLine.Parameters(index = "0", paramLabel = "PRODUCT_ARCHITECTURE_PATH", description = "Product architecture root where product-architecture.yml is located.")
    File productArchitectureDirectory;
    private final String manifestFileName;

    @Getter
    @CommandLine.Spec
    private CommandLine.Model.CommandSpec spec;

    @VisibleForTesting
    public ValidateCommand(File productArchitectureDirectory, String manifestFileName) {
        this.productArchitectureDirectory = productArchitectureDirectory;
        this.manifestFileName = manifestFileName;
    }

    public ValidateCommand() {
        this.manifestFileName = "product-architecture.yml";
    }

    @Override
    // TODO [TESTING]: add sad path coverage e2e tests
    public Integer call() throws IOException {
        List<String> messageSet = ArchitectureDataStructureValidatorFactory.create().validate(productArchitectureDirectory, this.manifestFileName);

        if (messageSet.isEmpty()) {
            print(manifestFileName + " is valid.");
        } else {
            printError(String.format("%s is invalid. (%d)", manifestFileName, messageSet.size()));
            messageSet.forEach(this::printError);
        }

        return messageSet.size();
    }
}
