package net.nahknarmi.arch.transformation;

import com.structurizr.Workspace;
import net.nahknarmi.arch.model.ArchitectureDataStructure;

public class ArchitectureDataStructureTransformer {


    public Workspace toWorkSpace(ArchitectureDataStructure dataStructure) {
        Workspace workspace = new Workspace(dataStructure.getName(), dataStructure.getDescription());
        workspace.setId(dataStructure.getId());
//        workspace.getDocumentation().getSections().add()

        return workspace;
    }
}
