package net.nahknarmi.arch.transformation;

import com.structurizr.Workspace;
import com.structurizr.documentation.AutomaticDocumentationTemplate;
import com.structurizr.documentation.DecisionStatus;
import net.nahknarmi.arch.model.ArchitectureDataStructure;

import java.io.File;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.structurizr.documentation.DecisionStatus.Deprecated;
import static com.structurizr.documentation.DecisionStatus.*;
import static com.structurizr.documentation.Format.Markdown;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

public class ArchitectureDataStructureTransformer {
    private final File documentationRoot;

    public ArchitectureDataStructureTransformer(File documentationRoot) {
        this.documentationRoot = documentationRoot;
    }

    public Workspace toWorkSpace(ArchitectureDataStructure dataStructure) throws IOException {
        checkNotNull(dataStructure, "ArchitectureDataStructure must not be null.");

        Workspace workspace = new Workspace(dataStructure.getName(), dataStructure.getDescription());
        workspace.setId(dataStructure.getId());

        addDocumentation(workspace, dataStructure);
        addDecisions(workspace, dataStructure);

        return workspace;
    }

    private void addDocumentation(Workspace workspace, ArchitectureDataStructure dataStructure) throws IOException {
        new AutomaticDocumentationTemplate(workspace).addSections(documentationPath(dataStructure));
    }

    private void addDecisions(Workspace workspace, ArchitectureDataStructure dataStructure) {
        ofNullable(dataStructure.getDecisions())
                .orElse(emptyList())
                .forEach(d ->
                        workspace.getDocumentation()
                                .addDecision(d.getId(), d.getDate(), d.getTitle(), getDecisionStatus(d.getStatus()), Markdown, d.getContent()));
    }

    private DecisionStatus getDecisionStatus(String status) {
        DecisionStatus decisionStatus;
        switch (ofNullable(status).orElse(Proposed.name()).toLowerCase()) {
            case "accepted":
                decisionStatus = Accepted;
                break;
            case "superseded":
                decisionStatus = Superseded;
                break;
            case "deprecated":
                decisionStatus = Deprecated;
                break;
            case "rejected":
                decisionStatus = Rejected;
                break;
            default:
                decisionStatus = Proposed;
                break;
        }
        return decisionStatus;
    }

    private File documentationPath(ArchitectureDataStructure dataStructure) {
        String path = String.format("%s/%s/documentation/", documentationRoot.getAbsolutePath(), dataStructure.getName().toLowerCase());
        return new File(path);
    }
}
