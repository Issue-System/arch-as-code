package net.trilogy.arch.transformation;

import com.structurizr.model.Container;
import com.structurizr.model.ContainerInstance;
import com.structurizr.model.DeploymentNode;
import com.structurizr.model.Model;
import lombok.experimental.UtilityClass;
import net.trilogy.arch.domain.c4.C4ContainerInstance;
import net.trilogy.arch.domain.c4.C4DeploymentNode;
import net.trilogy.arch.domain.c4.C4Model;
import net.trilogy.arch.domain.c4.C4Reference;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@UtilityClass
public class DeploymentNodeTransformer {
    static public DeploymentNode addDeploymentNodeFromC4ToModel(C4DeploymentNode node, C4Model c4Model, Model model) {
        DeploymentNode deploymentNode = model.addDeploymentNode(node.getName(), node.getDescription(), node.getTechnology(), node.getInstances());
        addChildren(model, c4Model, deploymentNode, node);
        return deploymentNode;
    }

    private static void addChildren(Model model, C4Model c4Model, DeploymentNode deploymentNode, C4DeploymentNode c4Node) {
        List<C4ContainerInstance> containerInstances = c4Node.getContainerInstances();
        if (!containerInstances.isEmpty()) {
            containerInstances.forEach(c -> {
                C4Reference containerReference = c.getContainerReference();
                String id = c4Model.findEntityByReference(containerReference).getId();
                deploymentNode.add((Container) model.getElement(id));
            });
        }

        List<C4DeploymentNode> children = c4Node.getChildren();
        if (!children.isEmpty()) {
            children.forEach(child -> {
                DeploymentNode addedDeploymentNode = deploymentNode.addDeploymentNode(
                        child.getName(),
                        child.getDescription(),
                        child.getTechnology(),
                        child.getInstances()
                );

                addChildren(model, c4Model, addedDeploymentNode, child);
            });
        }
    }

    public static C4DeploymentNode toC4(DeploymentNode node) {
        C4DeploymentNode c4Node = C4DeploymentNode.builder()
                .id(node.getId())
                .name(node.getName())
                .environment(node.getEnvironment())
                .technology(node.getTechnology())
                .description(node.getDescription())
                .tags(Set.of())
                .instances(node.getInstances())
                .containerInstances(new ArrayList<>())
                .children(new ArrayList<>())
                .build();

        node.getContainerInstances().forEach(
                containerInstance -> c4Node.addContainerInstance(toC4(containerInstance))
        );

        node.getChildren().forEach(
                child -> c4Node.addChild(toC4(child))
        );

        return c4Node;
    }

    private static C4ContainerInstance toC4(ContainerInstance containerInstance) {
        return new C4ContainerInstance(containerInstance.getId(),
                containerInstance.getEnvironment(),
                new C4Reference(containerInstance.getContainerId(), null),
                containerInstance.getInstanceId());
    }
}
