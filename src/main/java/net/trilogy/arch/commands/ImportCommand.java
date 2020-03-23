package net.trilogy.arch.commands;

import net.trilogy.arch.adapter.in.WorkspaceReader;
import net.trilogy.arch.adapter.out.ArchitectureDataStructureWriter;
import net.trilogy.arch.domain.ArchitectureDataStructure;
import picocli.CommandLine;

import java.io.File;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "import", description = "Imports existing struturizr workspace")
public class ImportCommand implements Callable<Integer> {

    @CommandLine.Parameters(index = "0", paramLabel = "EXPORTED_WORKSPACE", description = "Exported structurizr workspace location.")
    private File exportedWorkspacePath;

    // Only for testing purposes
    public ImportCommand(File exportedWorkspacePath) {
        this.exportedWorkspacePath = exportedWorkspacePath;
    }

    public ImportCommand() {
    }

    @Override
    public Integer call() throws Exception {
        ArchitectureDataStructure dataStructure = new WorkspaceReader().load(this.exportedWorkspacePath);
        File exportedFile = new ArchitectureDataStructureWriter().export(dataStructure);
        System.out.println(exportedFile.getAbsolutePath());
        return new PublishCommand(exportedFile.getParentFile(), exportedFile.getName()).call();
    }
}
