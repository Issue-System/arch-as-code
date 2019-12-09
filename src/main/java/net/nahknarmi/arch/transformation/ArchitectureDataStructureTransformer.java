package net.nahknarmi.arch.transformation;

import com.structurizr.Workspace;
import com.structurizr.documentation.AutomaticDocumentationTemplate;
import net.nahknarmi.arch.model.ArchitectureDataStructure;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class ArchitectureDataStructureTransformer {


    public ArchitectureDataStructureTransformer() {
        System.out.println("In constructor!!");
    }

    public Workspace toWorkSpace(ArchitectureDataStructure dataStructure) throws IOException {
        System.out.println("Workspace.toWorkSpace()");
        Workspace workspace = new Workspace(dataStructure.getName(), dataStructure.getDescription());
        workspace.setId(dataStructure.getId());

        //add functional overview
        AutomaticDocumentationTemplate template = new AutomaticDocumentationTemplate(workspace);

        Class<? extends ArchitectureDataStructureTransformer> aClass = getClass();
        URL resource = aClass.getResource(String.format("/architecture/products/%s/documentation/", dataStructure.getName()));
        System.out.println("Resource: " + resource);

        template.addSections(new File(resource.getPath()));

        return workspace;
    }
}
