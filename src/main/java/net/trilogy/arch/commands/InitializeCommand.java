package net.trilogy.arch.commands;

import net.trilogy.arch.adapter.architectureYaml.ArchitectureDataStructureWriter;
import net.trilogy.arch.domain.ArchitectureDataStructure;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import static net.trilogy.arch.adapter.structurizr.Credentials.createCredentials;

@CommandLine.Command(name = "init", description = "Initializes a new workspace directory to contain a single project architecture, AUs, documentation, and credentials for Structurizr imports and exports. This is generally the first command to be run.", mixinStandardHelpOptions = true)
public class InitializeCommand implements Callable<Integer> {

    @CommandLine.Option(names = {"-i", "--workspace-id"}, description = "Structurizr workspace id", required = true)
    private String workspaceId;

    @CommandLine.Option(names = {"-k", "--workspace-api-key"}, description = "Structurizr workspace api key", required = true)
    private String apiKey;

    @CommandLine.Option(names = {"-s", "--workspace-api-secret"}, description = "Structurizr workspace api secret", required = true)
    private String apiSecret;

    @CommandLine.Parameters(index = "0", description = "Directory to initialize")
    private File productArchitectureDirectory;

    @CommandLine.Spec
    private CommandLine.Model.CommandSpec spec;

    // for testing purposes
    public InitializeCommand(String workspaceId, String apiKey, String apiSecret, File productArchitectureDirectory) {
        this.workspaceId = workspaceId;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.productArchitectureDirectory = productArchitectureDirectory;
    }

    public InitializeCommand() {

    }

    @Override
    public Integer call() throws Exception {
        spec.commandLine().getOut().println(String.format("Architecture as code initialized under - %s", productArchitectureDirectory.getAbsolutePath()));

        // TODO [TESTING]: Add sad path e2e testing
        createCredentials(productArchitectureDirectory, workspaceId, apiKey, apiSecret);
        createManifest();

        spec.commandLine().getOut().println("You're ready to go!!");

        return 0;
    }

    private void createManifest() throws IOException {
        ArchitectureDataStructure data = createSampleDataStructure();
        String toFilePath = productArchitectureDirectory.getAbsolutePath() + File.separator + "product-architecture.yml";
        write(data, toFilePath);
        spec.commandLine().getOut().println("Manifest file written to - " + toFilePath);
    }

    private void write(ArchitectureDataStructure data, String toFilePath) throws IOException {
        File manifestFile = new File(toFilePath);
        new ArchitectureDataStructureWriter().export(data, manifestFile);
    }

    private ArchitectureDataStructure createSampleDataStructure() {
        ArchitectureDataStructure dataStructure = new ArchitectureDataStructure();
        dataStructure.setDescription("Architecture as code");
        dataStructure.setName("Hello World!!!");
        dataStructure.setBusinessUnit("DevFactory");
        return dataStructure;
    }
}
