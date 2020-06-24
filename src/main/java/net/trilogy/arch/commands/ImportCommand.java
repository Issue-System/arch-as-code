package net.trilogy.arch.commands;

import com.google.common.annotations.VisibleForTesting;
import lombok.Getter;
import net.trilogy.arch.adapter.architectureYaml.ArchitectureDataStructureWriter;
import net.trilogy.arch.adapter.structurizr.WorkspaceReader;
import net.trilogy.arch.commands.mixin.DisplaysOutputMixin;
import net.trilogy.arch.domain.ArchitectureDataStructure;
import picocli.CommandLine;

import java.io.File;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "import", mixinStandardHelpOptions = true, description = "Imports existing structurizr workspace, overwriting the existing product architecture.")
public class ImportCommand implements Callable<Integer>, DisplaysOutputMixin {

    @CommandLine.Parameters(index = "0", paramLabel = "EXPORTED_WORKSPACE", description = "Exported structurizr workspace json file location.")
    private File exportedWorkspacePath;

    @CommandLine.Parameters(index = "1", description = "Product architecture root directory")
    private File productArchitectureDirectory;

    @Getter
    @CommandLine.Spec
    private CommandLine.Model.CommandSpec spec;

    @Override
    // TODO: [TESTING] Sad path
    public Integer call() throws Exception {
        ArchitectureDataStructure dataStructure = new WorkspaceReader().load(this.exportedWorkspacePath);
        File writeFile = this.productArchitectureDirectory.toPath().resolve("product-architecture.yml").toFile();

        File exportedFile = new ArchitectureDataStructureWriter().export(dataStructure, writeFile);
        print(String.format("Architecture data structure written to - %s", exportedFile.getAbsolutePath()));
        return 0;
    }
}
