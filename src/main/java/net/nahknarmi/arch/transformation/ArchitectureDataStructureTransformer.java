package net.nahknarmi.arch.transformation;

import com.structurizr.Workspace;
import net.nahknarmi.arch.adapter.WorkspaceIdFinder;
import net.nahknarmi.arch.domain.ArchitectureDataStructure;
import net.nahknarmi.arch.transformation.enhancer.WorkspaceEnhancer;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class ArchitectureDataStructureTransformer {
    private final List<WorkspaceEnhancer> enhancers;
    private final WorkspaceIdFinder workspaceIdFinder;

    public ArchitectureDataStructureTransformer(List<WorkspaceEnhancer> enhancers, WorkspaceIdFinder workspaceIdFinder) {
        this.enhancers = enhancers;
        this.workspaceIdFinder = workspaceIdFinder;
    }

    public Workspace toWorkSpace(ArchitectureDataStructure dataStructure) {
        checkNotNull(dataStructure, "ArchitectureDataStructure must not be null.");

        Workspace workspace = new Workspace(dataStructure.getName(), dataStructure.getDescription());
        workspace.setId(workspaceIdFinder.workspaceId().orElseThrow(() -> new IllegalStateException("Workspace Id not found!!")));

        this.enhancers.forEach(e -> e.enhance(workspace, dataStructure));
        return workspace;
    }
}
