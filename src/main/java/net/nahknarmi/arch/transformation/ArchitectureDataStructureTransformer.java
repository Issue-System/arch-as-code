package net.nahknarmi.arch.transformation;

import com.structurizr.Workspace;
import com.structurizr.documentation.AutomaticDocumentationTemplate;
import net.nahknarmi.arch.model.ArchitectureDataStructure;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class ArchitectureDataStructureTransformer {

    public Workspace toWorkSpace(ArchitectureDataStructure dataStructure) throws IOException {
        Workspace workspace = new Workspace(dataStructure.getName(), dataStructure.getDescription());
        workspace.setId(dataStructure.getId());

        // Add Documentation
        AutomaticDocumentationTemplate template = new AutomaticDocumentationTemplate(workspace);

        String productName = dataStructure.getName().toLowerCase();
        URL documentationResource = getClass().getResource(String.format("/architecture/products/%s/documentation/", productName));

        template.addSections(new File(documentationResource.getPath()));

        // TODO: Add Decisions


        return workspace;
    }
}
