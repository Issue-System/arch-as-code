package net.trilogy.arch.domain.c4;

import com.structurizr.model.*;

public enum C4Type {
    RELATIONSHIP,
    PERSON,
    SYSTEM,
    CONTAINER,
    COMPONENT,
    DEPLOYMENT_NODE,
    CONTAINER_INSTANCE;

    public static C4Type from(Element element) {
        if (element instanceof Person) {
            return PERSON;
        } else if (element instanceof SoftwareSystem) {
            return SYSTEM;
        } else if (element instanceof Container) {
            return CONTAINER;
        } else if (element instanceof Component) {
            return COMPONENT;
        } else if (element instanceof DeploymentNode) {
            return DEPLOYMENT_NODE;
        } else if (element instanceof ContainerInstance) {
            return CONTAINER_INSTANCE;
        } else {
            throw new IllegalArgumentException("Unrecognized element type - " + element.getClass().getCanonicalName());
        }
    }

}
