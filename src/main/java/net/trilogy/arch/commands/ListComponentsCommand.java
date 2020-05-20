package net.trilogy.arch.commands;

import net.trilogy.arch.adapter.FilesFacade;
import net.trilogy.arch.adapter.in.ArchitectureDataStructureReader;
import net.trilogy.arch.domain.ArchitectureDataStructure;
import net.trilogy.arch.validation.ArchitectureDataStructureValidatorFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import lombok.Getter;
import picocli.CommandLine;
import picocli.CommandLine.Spec;
import picocli.CommandLine.Model.CommandSpec;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import com.google.common.annotations.VisibleForTesting;

@CommandLine.Command(name = "list-components", mixinStandardHelpOptions = true, description = "Outputs a CSV formatted list of components and their IDs, which are present in the architecture.")
public class ListComponentsCommand implements Callable<Integer>, DisplaysErrorMixin {

    @Getter
    @Spec
    private CommandSpec spec;

    @CommandLine.Parameters(index = "0", description = "Directory containing the product architecture")
    private File productArchitectureDirectory;

    private final ArchitectureDataStructureReader reader;

    public ListComponentsCommand( FilesFacade filesFacade ) {
        this.reader = new ArchitectureDataStructureReader(filesFacade);
    }

    @Override
    public Integer call() {

        ArchitectureDataStructure arch = null;
        try {
            arch = readArchitecture();
        } catch (Exception e) {
            printError("Unable to load architecture", e);
            return 1;
        }

        outputComponents(arch);

        return 0;
    }

    private void outputComponents(ArchitectureDataStructure arch) {
        String toOutput = arch.getModel()
                            .getComponents()
                            .stream()
                            .sorted((a, b) -> a.getId().compareTo(b.getId()))
                            .map(component -> 
                                    "\n" +
                                    component.getId() + ", " + 
                                    component.getName() + ", " + 
                                    (component.getPath() == null ? "" : component.getPath().getPath())
                            )
                            .collect(Collectors.joining());

        spec.commandLine().getOut().println("ID, Name, Path" + toOutput);
    }

    private ArchitectureDataStructure readArchitecture() throws IOException {
        return reader.load(
                productArchitectureDirectory.toPath()
                    .resolve("product-architecture.yml")
                    .toFile()
        );
    }
}
