package net.nahknarmi.arch;

import com.structurizr.Workspace;
import com.structurizr.api.StructurizrClientException;
import net.nahknarmi.arch.adapter.StructurizrAdapter;
import net.nahknarmi.arch.model.ArchitectureDataStructure;
import net.nahknarmi.arch.model.ArchitectureDataStructureImporter;
import net.nahknarmi.arch.transformation.ArchitectureDataStructureTransformer;

import java.io.IOException;
import java.io.InputStream;

public class StructurizrPublisher {
    private static final int PRODUCTION_WORKSPACE = 49328;
    private static final String ARCHITECTURE_DATA_STRUCTURE_PATH = "/architecture/products/devspaces/data-structure.yml";

    public StructurizrPublisher() {
    }

    public void publish(long workspaceId, InputStream manifest) throws StructurizrClientException, IOException {
        ArchitectureDataStructure dataStructure = new ArchitectureDataStructureImporter().load(manifest);
        Workspace workspace = new ArchitectureDataStructureTransformer().toWorkSpace(dataStructure);

        new StructurizrAdapter(workspaceId).publish(workspace);
    }

    public static void main(String[] args) throws Exception {
        new StructurizrPublisher().publish(PRODUCTION_WORKSPACE,
                StructurizrPublisher.class.getResourceAsStream(ARCHITECTURE_DATA_STRUCTURE_PATH));
    }
}
