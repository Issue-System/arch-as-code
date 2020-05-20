package net.trilogy.arch.publish;

import com.structurizr.Workspace;
import com.structurizr.api.StructurizrClientException;
import net.trilogy.arch.adapter.FilesFacade;
import net.trilogy.arch.adapter.StructurizrAdapter;
import net.trilogy.arch.adapter.in.ArchitectureDataStructureReader;
import net.trilogy.arch.domain.ArchitectureDataStructure;
import net.trilogy.arch.transformation.ArchitectureDataStructureTransformer;
import net.trilogy.arch.transformation.TransformerFactory;

import java.io.File;
import java.io.IOException;

public class ArchitectureDataStructurePublisher {
    private final File productArchitectureDirectory;
    private final ArchitectureDataStructureReader dataStructureReader;
    private final ArchitectureDataStructureTransformer dataStructureTransformer;
    private final StructurizrAdapter structurizrAdapter;
    private final String manifestFileName;

    ArchitectureDataStructurePublisher(File productArchitectureDirectory,
                                              ArchitectureDataStructureReader importer,
                                              ArchitectureDataStructureTransformer transformer,
                                              StructurizrAdapter structurizrAdapter) {
        this.productArchitectureDirectory = productArchitectureDirectory;
        this.dataStructureTransformer = transformer;
        this.dataStructureReader = importer;
        this.structurizrAdapter = structurizrAdapter;
        this.manifestFileName = "product-architecture.yml";

    }

    public ArchitectureDataStructurePublisher(File productArchitectureDirectory, ArchitectureDataStructureReader importer, ArchitectureDataStructureTransformer transformer, StructurizrAdapter adapter, String manifestFileName) {
        this.productArchitectureDirectory = productArchitectureDirectory;
        this.dataStructureTransformer = transformer;
        this.dataStructureReader = importer;
        this.structurizrAdapter = adapter;
        this.manifestFileName = manifestFileName;
    }

    // TODO [TESTING] [OVERHAUL] [OPTIONAL]: Make testing not require real connection to Structurizr.
    public void publish() throws StructurizrClientException, IOException {
        Workspace workspace = getWorkspace(productArchitectureDirectory, manifestFileName);
        structurizrAdapter.publish(workspace);
    }

    public Workspace getWorkspace(File productArchitectureDirectory, String manifestFileName) throws IOException {
        File manifestFile = new File(productArchitectureDirectory + File.separator + manifestFileName);
        ArchitectureDataStructure dataStructure = dataStructureReader.load(manifestFile);

        return dataStructureTransformer.toWorkSpace(dataStructure);
    }

    public static ArchitectureDataStructurePublisher create(File productArchitectureDirectory, String manifestFileName) {
        ArchitectureDataStructureReader importer = new ArchitectureDataStructureReader(new FilesFacade());
        ArchitectureDataStructureTransformer transformer = TransformerFactory.create(productArchitectureDirectory);
        StructurizrAdapter adapter = new StructurizrAdapter();

        return new ArchitectureDataStructurePublisher(productArchitectureDirectory, importer, transformer, adapter, manifestFileName);
    }
}
