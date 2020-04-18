package net.trilogy.arch.commands;

import com.structurizr.Workspace;
import com.structurizr.util.WorkspaceUtils;
import net.trilogy.arch.adapter.in.WorkspaceReader;
import net.trilogy.arch.adapter.out.ArchitectureDataStructureWriter;
import net.trilogy.arch.domain.ArchitectureDataStructure;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.h2.Driver;
import picocli.CommandLine;

import java.io.File;
import java.sql.Connection;
import java.util.Properties;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "import", description = "Imports existing structurizr workspace")
public class ImportCommand implements Callable<Integer> {
    private static final Log logger = LogFactory.getLog(ArchitectureDataStructureWriter.class);

    @CommandLine.Parameters(index = "0", paramLabel = "EXPORTED_WORKSPACE", description = "Exported structurizr workspace location.")
    private File exportedWorkspacePath;

    @CommandLine.Parameters(index = "1", description = "Product documentation root directory")
    private File productDocumentationRoot;


    // Only for testing purposes
    public ImportCommand(File exportedWorkspacePath) {
        this.exportedWorkspacePath = exportedWorkspacePath;
    }

    public ImportCommand() {
    }

    @Override
    public Integer call() throws Exception {
        Workspace workspace = WorkspaceUtils.loadWorkspaceFromJson(this.exportedWorkspacePath);

        Connection connection = Driver.load().connect("jdbc:h2:mem:aac", new Properties());
        new WorkspaceReader().loadSql(this.exportedWorkspacePath, connection);
        File databaseCsvExported = this.productDocumentationRoot.toPath().resolve("architecture.csv").toFile();
        File exportedCsv = new ArchitectureDataStructureWriter().export(connection, databaseCsvExported);
        connection.close();

        logger.info(String.format("Architecture csv written to - %s", exportedCsv.getAbsolutePath()));


        ArchitectureDataStructure dataStructure = new WorkspaceReader().load(this.exportedWorkspacePath);
        File writeFile = this.productDocumentationRoot.toPath().resolve("data-structure.yml").toFile();

        File exportedFile = new ArchitectureDataStructureWriter().export(dataStructure, writeFile);
        logger.info(String.format("Architecture data structure written to - %s", exportedFile.getAbsolutePath()));
        return 0;
    }

}
