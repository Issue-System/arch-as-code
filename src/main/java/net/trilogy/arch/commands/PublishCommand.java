package net.trilogy.arch.commands;

import com.google.common.annotations.VisibleForTesting;
import com.structurizr.api.StructurizrClientException;
import net.trilogy.arch.publish.ArchitectureDataStructurePublisher;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "publish", mixinStandardHelpOptions = true, description = "Publish architecture to structurizr.")
public class PublishCommand implements Callable<Integer> {
    private final String manifestFileName;

    @CommandLine.Parameters(index = "0", paramLabel = "PRODUCT_ARCHITECTURE_DIRECTORY", description = "Product architecture root where data-structure.yml is located.")
    private File productArchitectureDirectory;

    @VisibleForTesting
    public PublishCommand(File productArchitectureDirectory, String manifestFileName) {
        this.productArchitectureDirectory = productArchitectureDirectory;
        this.manifestFileName = manifestFileName;
    }

    public PublishCommand() {
        this.manifestFileName = "data-structure.yml";
    }

    @Override
    // TODO: [TESTING] Sad path
    public Integer call() throws IOException, StructurizrClientException {
        if (new ValidateCommand(productArchitectureDirectory, manifestFileName).call() == 0) {
            ArchitectureDataStructurePublisher.create(productArchitectureDirectory, manifestFileName).publish();
            return 0;
        }
        return 1;
    }
}

