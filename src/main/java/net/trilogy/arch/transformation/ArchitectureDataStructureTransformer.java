package net.trilogy.arch.transformation;

import com.structurizr.Workspace;
import net.trilogy.arch.adapter.structurizr.Credentials;
import net.trilogy.arch.domain.ArchitectureDataStructure;
import net.trilogy.arch.transformation.enhancer.WorkspaceEnhancer;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class ArchitectureDataStructureTransformer {
    private final List<WorkspaceEnhancer> enhancers;

    public ArchitectureDataStructureTransformer(List<WorkspaceEnhancer> enhancers) {
        this.enhancers = enhancers;
    }

    public Workspace toWorkSpace(ArchitectureDataStructure dataStructure) {
        checkNotNull(dataStructure, "ArchitectureDataStructure must not be null.");

        Workspace workspace = new Workspace(dataStructure.getName(), dataStructure.getDescription());
        workspace.setId(Credentials.config().getWorkspaceId());

        this.enhancers.forEach(e -> e.enhance(workspace, dataStructure));
        return workspace;
    }
}
