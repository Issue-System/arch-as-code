package net.trilogy.arch.transformation.enhancer;

import com.structurizr.Workspace;
import com.structurizr.documentation.AutomaticDocumentationTemplate;
import com.structurizr.documentation.Section;
import net.trilogy.arch.domain.ArchitectureDataStructure;
import net.trilogy.arch.domain.DocumentationImage;
import net.trilogy.arch.domain.DocumentationSection;
import net.trilogy.arch.facade.FilesFacade;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class DocumentationEnhancer implements WorkspaceEnhancer {
    private final File documentationRoot;
    private final FilesFacade filesFacade;

    public DocumentationEnhancer(File documentationRoot, FilesFacade filesFacade) {
        this.documentationRoot = documentationRoot;
        this.filesFacade = filesFacade;
    }

    @Override
    public void enhance(Workspace workspace, ArchitectureDataStructure dataStructure) {
        if (!documentationPath().exists()) return;

        final AutomaticDocumentationTemplate docTemplate = new AutomaticDocumentationTemplate(workspace);
        final File[] files = documentationPath().listFiles();
        final Set<DocumentationSection> docsToAdd = Arrays.stream(files)
                .filter(file -> isDocumentation(file))
                .map(f -> createDocFromFile(f))
                .filter(doc -> doc != null)
                .collect(Collectors.toSet());

        // Add ordered documentation
        docsToAdd.stream()
                .filter(doc -> doc.getOrder() != null)
                .forEach(doc -> addDocumentationToWorkspace(docTemplate, doc));


        // Add un-ordered documentation
        docsToAdd.stream()
                .filter(doc -> doc.getOrder() == null)
                .forEach(doc -> addDocumentationToWorkspace(docTemplate, doc));
    }

    private boolean isDocumentation(File file) {
        if (file == null) return false;
        if (file.isDirectory()) return false;
        if (DocumentationImage.isImage(file)) return false;

        return true;
    }

    private void addDocumentationToWorkspace(AutomaticDocumentationTemplate docTemplate, DocumentationSection doc) {
        if (doc != null) {
            final Section section = docTemplate.addSection(doc.getTitle(), doc.getStructurizrFormat(), doc.getContent());

            if (doc.getOrder() != null) section.setOrder(doc.getOrder());
        }
    }

    private DocumentationSection createDocFromFile(File file) {
        if (file == null) return null;
        if (file.isDirectory()) return null;

        DocumentationSection doc = null;
        try {
            doc = DocumentationSection.createFromFile(file, filesFacade);
        } catch (IOException e) {
            System.err.println("Unable to import documentation: " + file.getName() + "\nError thrown: " + e);
        }

        return doc;
    }


    private File documentationPath() {
        return new File(String.format("%s/documentation/", documentationRoot.getAbsolutePath()));
    }
}
