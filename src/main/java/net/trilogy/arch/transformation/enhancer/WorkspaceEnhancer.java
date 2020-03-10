package net.trilogy.arch.transformation.enhancer;

import com.structurizr.Workspace;
import net.trilogy.arch.domain.ArchitectureDataStructure;

public interface WorkspaceEnhancer {
    void enhance(Workspace workspace, ArchitectureDataStructure dataStructure);
}
