package net.trilogy.arch.domain.c4;

import com.structurizr.model.*;

public enum C4Type {
    person,
    system,
    container,
    component,
    deploymentNode,
    containerInstance;

    public static C4Type from(Element element) {
        if (element instanceof Person) {
            return person;
        } else if (element instanceof SoftwareSystem) {
            return system;
        } else if (element instanceof Container) {
            return container;
        } else if (element instanceof Component) {
            return component;
        } else if (element instanceof DeploymentNode) {
            return deploymentNode;
        } else if (element instanceof ContainerInstance) {
            return containerInstance;
        } else {
            throw new IllegalArgumentException("Unrecognized element type - " + element.getClass().getCanonicalName());
        }
    }

}
