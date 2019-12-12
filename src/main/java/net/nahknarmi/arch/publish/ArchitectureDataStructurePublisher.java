package net.nahknarmi.arch.publish;

import com.structurizr.Workspace;
import com.structurizr.api.StructurizrClientException;
import net.nahknarmi.arch.adapter.StructurizrAdapter;
import net.nahknarmi.arch.model.ArchitectureDataStructure;
import net.nahknarmi.arch.model.ArchitectureDataStructureReader;
import net.nahknarmi.arch.transformation.ArchitectureDataStructureTransformer;
import net.nahknarmi.arch.transformation.TransformerFactory;

import java.io.File;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

public class ArchitectureDataStructurePublisher {
    private final File productDocumentationRoot;
    private final ArchitectureDataStructureReader dataStructureReader;
    private final ArchitectureDataStructureTransformer dataStructureTransformer;
    private final StructurizrAdapter structurizrAdapter;

    ArchitectureDataStructurePublisher(File productDocumentationRoot,
                                              ArchitectureDataStructureReader importer,
                                              ArchitectureDataStructureTransformer transformer,
                                              StructurizrAdapter structurizrAdapter) {
        this.productDocumentationRoot = productDocumentationRoot;
        this.dataStructureTransformer = transformer;
        this.dataStructureReader = importer;
        this.structurizrAdapter = structurizrAdapter;
    }

    public void publish(String productName) throws StructurizrClientException, IOException {
        checkNotNull(productName, "Product name must not be null.");

        File manifestFile = new File(productDocumentationRoot + File.separator + productName.toLowerCase() + File.separator + "data-structure.yml");

        ArchitectureDataStructure dataStructure = dataStructureReader.load(manifestFile);
        Workspace workspace = dataStructureTransformer.toWorkSpace(dataStructure);

        structurizrAdapter.publish(workspace);
    }

    public static ArchitectureDataStructurePublisher create(File productDocumentationRoot) {
        ArchitectureDataStructureReader importer = new ArchitectureDataStructureReader();
        ArchitectureDataStructureTransformer transformer = TransformerFactory.create(productDocumentationRoot);
        StructurizrAdapter adapter = new StructurizrAdapter();

        return new ArchitectureDataStructurePublisher(productDocumentationRoot, importer, transformer, adapter);
    }
}
