package net.nahknarmi.arch.transformation;

import com.structurizr.Workspace;
import com.structurizr.model.Element;

public class TransformationHelper {
    public static Element getElementWithName(Workspace workspace, String name) {
        return workspace.getModel().getElements().stream().filter(e -> e.getName().equals(name)).findFirst().get();
    }
}
