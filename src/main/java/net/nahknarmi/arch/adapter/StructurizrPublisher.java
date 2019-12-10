package net.nahknarmi.arch.adapter;

import com.structurizr.Workspace;
import net.nahknarmi.arch.model.ArchitectureDataStructure;
import net.nahknarmi.arch.model.ArchitectureDataStructureImporter;
import net.nahknarmi.arch.transformation.ArchitectureDataStructureTransformer;

import java.io.InputStream;

public class StructurizrPublisher {
    private static final int PRODUCTION_WORKSPACE = 49328;

    public static void main(String[] args) throws Exception {
        InputStream architectureManifestYaml =
                StructurizrPublisher.class.getResourceAsStream("/architecture/products/devspaces/data-structure.yml");

        ArchitectureDataStructureImporter importer = new ArchitectureDataStructureImporter();
        ArchitectureDataStructure dataStructure = importer.load(architectureManifestYaml);

        ArchitectureDataStructureTransformer architectureDataStructureTransformer = new ArchitectureDataStructureTransformer();
        Workspace workspace = architectureDataStructureTransformer.toWorkSpace(dataStructure);

        new StructurizrAdapter(PRODUCTION_WORKSPACE).publish(workspace);
    }
}
