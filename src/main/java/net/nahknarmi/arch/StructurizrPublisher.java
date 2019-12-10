package net.nahknarmi.arch;

import com.structurizr.Workspace;
import net.nahknarmi.arch.adapter.StructurizrAdapter;
import net.nahknarmi.arch.model.ArchitectureDataStructure;
import net.nahknarmi.arch.model.ArchitectureDataStructureImporter;
import net.nahknarmi.arch.transformation.ArchitectureDataStructureTransformer;

import java.io.InputStream;

public class StructurizrPublisher {
    private static final int PRODUCTION_WORKSPACE = 49328;

    public static void main(String[] args) throws Exception {
        InputStream manifest =
                StructurizrPublisher.class.getResourceAsStream("/architecture/products/devspaces/data-structure.yml");

        ArchitectureDataStructure dataStructure = new ArchitectureDataStructureImporter().load(manifest);
        Workspace workspace = new ArchitectureDataStructureTransformer().toWorkSpace(dataStructure);

        new StructurizrAdapter(PRODUCTION_WORKSPACE).publish(workspace);
    }
}
