package net.trilogy.arch.commands;

import lombok.Getter;
import net.trilogy.arch.adapter.structurizr.StructurizrAdapter;
import net.trilogy.arch.commands.mixin.DisplaysErrorMixin;
import net.trilogy.arch.commands.mixin.DisplaysOutputMixin;
import net.trilogy.arch.facade.FilesFacade;
import net.trilogy.arch.publish.ArchitectureDataStructurePublisher;
import net.trilogy.arch.validation.ArchitectureDataStructureValidatorFactory;
import picocli.CommandLine;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "publish", mixinStandardHelpOptions = true, description = "Publish architecture to structurizr.")
public class PublishCommand implements Callable<Integer>, DisplaysOutputMixin, DisplaysErrorMixin {
    private final String manifestFileName;
    private final StructurizrAdapter structurizrAdapter;

    @CommandLine.Parameters(index = "0", paramLabel = "PRODUCT_ARCHITECTURE_DIRECTORY", description = "Product architecture root where product-architecture.yml is located.")
    private File productArchitectureDirectory;

    @Getter
    @CommandLine.Spec
    private CommandLine.Model.CommandSpec spec;

    public PublishCommand() {
        this.manifestFileName = "product-architecture.yml";
        this.structurizrAdapter = new StructurizrAdapter();
    }

    public PublishCommand(StructurizrAdapter structurizrAdapter) {
        this.manifestFileName = "product-architecture.yml";
        this.structurizrAdapter = structurizrAdapter;
    }

    @Override
    public Integer call() {
        logArgs();
        List<String> messageSet = List.of();
        try {
            messageSet = ArchitectureDataStructureValidatorFactory.create().validate(productArchitectureDirectory, this.manifestFileName);

            if (messageSet.isEmpty()) {
                new ArchitectureDataStructurePublisher(structurizrAdapter, new FilesFacade(), productArchitectureDirectory, manifestFileName).publish();
                print("Successfully published to Structurizr!");
                return 0;
            }
        } catch (Exception e) {
            printError("Unable to publish to Structurizer", e);
            return 1;
        }

        printError(String.format("Invalid product-architecture.yml has %d errors:", messageSet.size()));
        messageSet.forEach(this::printError);
        return messageSet.size();
    }
}

