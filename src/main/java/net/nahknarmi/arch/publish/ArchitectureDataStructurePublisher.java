package net.nahknarmi.arch.publish;

import com.structurizr.Workspace;
import com.structurizr.api.StructurizrClientException;
import net.nahknarmi.arch.adapter.StructurizrAdapter;
import net.nahknarmi.arch.adapter.WorkspaceIdFinder;
import net.nahknarmi.arch.domain.ArchitectureDataStructure;
import net.nahknarmi.arch.domain.ArchitectureDataStructureReader;
import net.nahknarmi.arch.transformation.ArchitectureDataStructureTransformer;
import net.nahknarmi.arch.transformation.TransformerFactory;

import java.io.File;
import java.io.IOException;

public class ArchitectureDataStructurePublisher {
    private final File productDocumentationRoot;
    private final ArchitectureDataStructureReader dataStructureReader;
    private final ArchitectureDataStructureTransformer dataStructureTransformer;
    private final StructurizrAdapter structurizrAdapter;
    private final String manifiestFileName;

    ArchitectureDataStructurePublisher(File productDocumentationRoot,
                                              ArchitectureDataStructureReader importer,
                                              ArchitectureDataStructureTransformer transformer,
                                              StructurizrAdapter structurizrAdapter) {
        this.productDocumentationRoot = productDocumentationRoot;
        this.dataStructureTransformer = transformer;
        this.dataStructureReader = importer;
        this.structurizrAdapter = structurizrAdapter;
        this.manifiestFileName = "data-structure.yml";

    }

    public ArchitectureDataStructurePublisher(File productDocumentationRoot, ArchitectureDataStructureReader importer, ArchitectureDataStructureTransformer transformer, StructurizrAdapter adapter, String manifestFileName) {
        this.productDocumentationRoot = productDocumentationRoot;
        this.dataStructureTransformer = transformer;
        this.dataStructureReader = importer;
        this.structurizrAdapter = adapter;
        this.manifiestFileName = manifestFileName;
    }

    public void publish() throws StructurizrClientException, IOException {
        File manifestFile = new File(productDocumentationRoot + File.separator + manifiestFileName);

        ArchitectureDataStructure dataStructure = dataStructureReader.load(manifestFile);
        Workspace workspace = dataStructureTransformer.toWorkSpace(dataStructure);

        structurizrAdapter.publish(workspace);
    }

    public static ArchitectureDataStructurePublisher create(File productDocumentationRoot, String manifestFileName) {
        ArchitectureDataStructureReader importer = new ArchitectureDataStructureReader();
        ArchitectureDataStructureTransformer transformer = TransformerFactory.create(productDocumentationRoot);
        WorkspaceIdFinder workspaceIdFinder = new WorkspaceIdFinder();
        StructurizrAdapter adapter = new StructurizrAdapter(workspaceIdFinder);

        return new ArchitectureDataStructurePublisher(productDocumentationRoot, importer, transformer, adapter, manifestFileName);
    }
}
