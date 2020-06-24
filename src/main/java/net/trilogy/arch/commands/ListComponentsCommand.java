package net.trilogy.arch.commands;

import net.trilogy.arch.adapter.architectureYaml.ArchitectureDataStructureObjectMapper;
import net.trilogy.arch.commands.mixin.DisplaysOutputMixin;
import net.trilogy.arch.commands.mixin.LoadArchitectureMixin;
import net.trilogy.arch.facade.FilesFacade;
import net.trilogy.arch.domain.ArchitectureDataStructure;

import lombok.Getter;
import picocli.CommandLine;
import picocli.CommandLine.Spec;
import picocli.CommandLine.Model.CommandSpec;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@CommandLine.Command(name = "list-components", mixinStandardHelpOptions = true, description = "Outputs a CSV formatted list of components and their IDs, which are present in the architecture.")
public class ListComponentsCommand implements Callable<Integer>, LoadArchitectureMixin, DisplaysOutputMixin {

    @Getter
    @Spec
    private CommandSpec spec;

    @Getter
    @CommandLine.Parameters(index = "0", description = "Directory containing the product architecture")
    private File productArchitectureDirectory;

    @Getter
    private final ArchitectureDataStructureObjectMapper architectureDataStructureObjectMapper;

    @Getter
    private final FilesFacade filesFacade;

    public ListComponentsCommand( FilesFacade filesFacade ) {
        this.filesFacade = filesFacade;
        this.architectureDataStructureObjectMapper = new ArchitectureDataStructureObjectMapper();
    }

    @Override
    public Integer call() {
        final var arch = loadArchitectureOrPrintError("Unable to load architecture");
        if(arch.isEmpty()) return 1;

        outputComponents(arch.get());

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

        print("ID, Name, Path" + toOutput);
    }
}
