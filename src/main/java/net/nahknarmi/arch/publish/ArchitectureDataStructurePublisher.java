package net.nahknarmi.arch.publish;

import com.structurizr.Workspace;
import com.structurizr.api.StructurizrClientException;
import net.nahknarmi.arch.adapter.StructurizrAdapter;
import net.nahknarmi.arch.model.ArchitectureDataStructure;
import net.nahknarmi.arch.model.ArchitectureDataStructureImporter;
import net.nahknarmi.arch.transformation.ArchitectureDataStructureTransformer;

import java.io.File;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class ArchitectureDataStructurePublisher {
    private final File productDocumentationRoot;

    public ArchitectureDataStructurePublisher(File productDocumentationRoot) {
        this.productDocumentationRoot = productDocumentationRoot;
    }

    public void publish(long workspaceId, String productName) throws StructurizrClientException, IOException {
        checkArgument(workspaceId > 0, "Workspace id must be greater than 0.");
        checkNotNull(productName, "Product name must not be null.");

        File manifestFile = new File(productDocumentationRoot + File.separator + productName.toLowerCase() + File.separator + "data-structure.yml");
        checkArgument(manifestFile.exists(), String.format("Manifest file %s does not exist.", manifestFile));

        ArchitectureDataStructure dataStructure = new ArchitectureDataStructureImporter().load(manifestFile);
        Workspace workspace = new ArchitectureDataStructureTransformer(this.productDocumentationRoot).toWorkSpace(dataStructure);

        new StructurizrAdapter(workspaceId).publish(workspace);
    }
}
