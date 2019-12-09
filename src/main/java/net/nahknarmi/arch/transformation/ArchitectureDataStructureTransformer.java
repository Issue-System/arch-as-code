package net.nahknarmi.arch.transformation;

import com.structurizr.Workspace;
import com.structurizr.documentation.AutomaticDocumentationTemplate;
import net.nahknarmi.arch.model.ArchitectureDataStructure;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class ArchitectureDataStructureTransformer {


    public Workspace toWorkSpace(ArchitectureDataStructure dataStructure) throws IOException {
        System.err.println("Workspace.toWorkSpace()");
        Workspace workspace = new Workspace(dataStructure.getName(), dataStructure.getDescription());
        workspace.setId(dataStructure.getId());

        //add functional overview
        AutomaticDocumentationTemplate template = new AutomaticDocumentationTemplate(workspace);

        URL resource = getClass().getResource(String.format("/architecture/products/%s/documentation/", dataStructure.getName()));
        template.addSections(new File(resource.getPath()));

        return workspace;
    }
}
