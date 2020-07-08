package net.trilogy.arch.publish;

import com.structurizr.Workspace;
import com.structurizr.api.StructurizrClientException;
import net.trilogy.arch.adapter.architectureYaml.ArchitectureDataStructureObjectMapper;
import net.trilogy.arch.adapter.architectureYaml.ArchitectureDataStructureReader;
import net.trilogy.arch.adapter.structurizr.StructurizrAdapter;
import net.trilogy.arch.domain.ArchitectureDataStructure;
import net.trilogy.arch.facade.FilesFacade;
import net.trilogy.arch.transformation.ArchitectureDataStructureTransformer;
import net.trilogy.arch.transformation.TransformerFactory;

import java.io.File;
import java.io.IOException;

public class ArchitectureDataStructurePublisher {
    private final File productArchitectureDirectory;
    private final ArchitectureDataStructureReader dataStructureReader;
    private final FilesFacade filesFacade;
    private final ArchitectureDataStructureTransformer dataStructureTransformer;
    private final StructurizrAdapter structurizrAdapter;
    private final String manifestFileName;

    ArchitectureDataStructurePublisher(File productArchitectureDirectory,
                                       ArchitectureDataStructureReader importer,
                                       FilesFacade filesFacade,
                                       ArchitectureDataStructureTransformer transformer,
                                       StructurizrAdapter structurizrAdapter) {
        this.productArchitectureDirectory = productArchitectureDirectory;
        this.dataStructureTransformer = transformer;
        this.dataStructureReader = importer;
        this.filesFacade = filesFacade;
        this.structurizrAdapter = structurizrAdapter;
        this.manifestFileName = "product-architecture.yml";

    }

    public ArchitectureDataStructurePublisher(File productArchitectureDirectory,
                                              ArchitectureDataStructureReader importer,
                                              FilesFacade filesFacade,
                                              ArchitectureDataStructureTransformer transformer,
                                              StructurizrAdapter adapter,
                                              String manifestFileName) {
        this.productArchitectureDirectory = productArchitectureDirectory;
        this.dataStructureTransformer = transformer;
        this.dataStructureReader = importer;
        this.filesFacade = filesFacade;
        this.structurizrAdapter = adapter;
        this.manifestFileName = manifestFileName;
    }

    public ArchitectureDataStructurePublisher(FilesFacade filesFacade,
                                              File productArchitectureDirectory,
                                              String manifestFileName) {
        this.filesFacade = filesFacade;
        this.productArchitectureDirectory = productArchitectureDirectory;
        this.manifestFileName = manifestFileName;
        this.dataStructureTransformer = TransformerFactory.create(productArchitectureDirectory);
        this.structurizrAdapter = new StructurizrAdapter();
        this.dataStructureReader = null;
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
        this.dataStructureReader = null;
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

    public static ArchitectureDataStructurePublisher create(FilesFacade filesFacade, File productArchitectureDirectory, String manifestFileName) {
        ArchitectureDataStructureReader importer = new ArchitectureDataStructureReader(filesFacade);
        ArchitectureDataStructureTransformer transformer = TransformerFactory.create(productArchitectureDirectory);
        StructurizrAdapter adapter = new StructurizrAdapter();

        return new ArchitectureDataStructurePublisher(productArchitectureDirectory, importer, filesFacade, transformer, adapter, manifestFileName);
    }
}
