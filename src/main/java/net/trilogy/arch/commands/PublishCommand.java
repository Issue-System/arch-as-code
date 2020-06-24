package net.trilogy.arch.commands;

import com.google.common.annotations.VisibleForTesting;
import com.structurizr.api.StructurizrClientException;
import net.trilogy.arch.publish.ArchitectureDataStructurePublisher;
import net.trilogy.arch.validation.ArchitectureDataStructureValidatorFactory;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "publish", mixinStandardHelpOptions = true, description = "Publish architecture to structurizr.")
public class PublishCommand implements Callable<Integer> {
    private final String manifestFileName;

    @CommandLine.Parameters(index = "0", paramLabel = "PRODUCT_ARCHITECTURE_DIRECTORY", description = "Product architecture root where product-architecture.yml is located.")
    private File productArchitectureDirectory;

    @VisibleForTesting
    public PublishCommand(File productArchitectureDirectory, String manifestFileName) {
        this.productArchitectureDirectory = productArchitectureDirectory;
        this.manifestFileName = manifestFileName;
    }

    public PublishCommand() {
        this.manifestFileName = "product-architecture.yml";
    }

    @Override
    // TODO: [TESTING] Sad path
    public Integer call() throws IOException, StructurizrClientException {
        List<String> messageSet = ArchitectureDataStructureValidatorFactory.create().validate(productArchitectureDirectory, this.manifestFileName);

        if (messageSet.isEmpty()) {
            ArchitectureDataStructurePublisher.create(productArchitectureDirectory, manifestFileName).publish();
            return 0;
        }
        return 1;
    }
}

