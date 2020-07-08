package net.trilogy.arch.publish;

import com.structurizr.Workspace;
import com.structurizr.api.StructurizrClientException;
import net.trilogy.arch.adapter.architectureYaml.ArchitectureDataStructureObjectMapper;
import net.trilogy.arch.adapter.structurizr.StructurizrAdapter;
import net.trilogy.arch.domain.ArchitectureDataStructure;
import net.trilogy.arch.facade.FilesFacade;
import net.trilogy.arch.transformation.ArchitectureDataStructureTransformer;
import net.trilogy.arch.transformation.TransformerFactory;

import java.io.File;
import java.io.IOException;

public class ArchitectureDataStructurePublisher {
    private final File productArchitectureDirectory;
    private final FilesFacade filesFacade;
    private final ArchitectureDataStructureTransformer dataStructureTransformer;
    private final StructurizrAdapter structurizrAdapter;
    private final String manifestFileName;

    public ArchitectureDataStructurePublisher(FilesFacade filesFacade,
                                              File productArchitectureDirectory,
                                              String manifestFileName) {
        this.filesFacade = filesFacade;
        this.productArchitectureDirectory = productArchitectureDirectory;
        this.manifestFileName = manifestFileName;
        this.dataStructureTransformer = TransformerFactory.create(productArchitectureDirectory);
        this.structurizrAdapter = new StructurizrAdapter();
    }

    public ArchitectureDataStructurePublisher(StructurizrAdapter structurizrAdapter,
                                              FilesFacade filesFacade,
                                              File productArchitectureDirectory,
                                              String manifestFileName) {
        this.filesFacade = filesFacade;
        this.productArchitectureDirectory = productArchitectureDirectory;
        this.manifestFileName = manifestFileName;
        this.dataStructureTransformer = TransformerFactory.create(productArchitectureDirectory);
        this.structurizrAdapter = structurizrAdapter;
    }

    // TODO [TESTING] [OVERHAUL] [OPTIONAL]: Make testing not require real connection to Structurizr.
    public void publish() throws StructurizrClientException, IOException {
        Workspace workspace = getWorkspace(productArchitectureDirectory, manifestFileName);
        structurizrAdapter.publish(workspace);
    }

    public Workspace getWorkspace(File productArchitectureDirectory, String manifestFileName) throws IOException {
        ArchitectureDataStructure dataStructure = loadProductArchitecture(productArchitectureDirectory, manifestFileName);

        return dataStructureTransformer.toWorkSpace(dataStructure);
    }

    public ArchitectureDataStructure loadProductArchitecture(File productArchitectureDirectory, String manifestFileName) throws IOException {
        File manifestFile = new File(productArchitectureDirectory + File.separator + manifestFileName);
        String archAsString = this.filesFacade.readString(manifestFile.toPath());

        return new ArchitectureDataStructureObjectMapper().readValue(archAsString);
    }
}
