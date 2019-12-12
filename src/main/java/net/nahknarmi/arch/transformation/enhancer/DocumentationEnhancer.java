package net.nahknarmi.arch.transformation.enhancer;

import com.structurizr.Workspace;
import com.structurizr.documentation.AutomaticDocumentationTemplate;
import net.nahknarmi.arch.domain.ArchitectureDataStructure;

import java.io.File;
import java.io.IOException;

public class DocumentationEnhancer implements WorkspaceEnhancer {
    private final File documentationRoot;

    public DocumentationEnhancer(File documentationRoot) {
        this.documentationRoot = documentationRoot;
    }

    @Override
    public void enhance(Workspace workspace, ArchitectureDataStructure dataStructure) {
        try {
            new AutomaticDocumentationTemplate(workspace).addSections(documentationPath(dataStructure));
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Unable to add documentation - %s", dataStructure), e);
        }
    }

    private File documentationPath(ArchitectureDataStructure dataStructure) {
        String path = String.format("%s/%s/documentation/", documentationRoot.getAbsolutePath(), dataStructure.getName().toLowerCase());
        return new File(path);
    }
}
