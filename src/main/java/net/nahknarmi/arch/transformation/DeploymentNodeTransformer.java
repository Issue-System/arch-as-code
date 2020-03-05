package net.nahknarmi.arch.transformation;

import com.structurizr.model.Container;
import com.structurizr.model.DeploymentNode;
import com.structurizr.model.Model;
import net.nahknarmi.arch.domain.c4.C4ContainerInstance;
import net.nahknarmi.arch.domain.c4.C4DeploymentNode;
import net.nahknarmi.arch.domain.c4.C4Model;
import net.nahknarmi.arch.domain.c4.C4Reference;

import java.util.List;

public class DeploymentNodeTransformer {
    static public DeploymentNode convertToStructurizrDeploymentNode(Model model, C4Model dataStructureModel, C4DeploymentNode c4DeploymentNode) {
        DeploymentNode deploymentNode = model.addDeploymentNode(c4DeploymentNode.getName(), c4DeploymentNode.getDescription(), c4DeploymentNode.getTechnology(), c4DeploymentNode.getInstances());
        addChildren(model, dataStructureModel, deploymentNode, c4DeploymentNode);

        return deploymentNode;
    }

    private static void addChildren(Model model, C4Model dataStructureModel, DeploymentNode deploymentNode, C4DeploymentNode c4DeploymentNode) {
        List<C4ContainerInstance> containerInstances = c4DeploymentNode.getContainerInstances();
        if (!containerInstances.isEmpty()) {
            containerInstances.forEach(c -> {
                C4Reference containerReference = c.getContainerReference();
                String id = dataStructureModel.findEntityByReference(containerReference).getId();
                deploymentNode.add((Container) model.getElement(id));
            });
        }

        List<C4DeploymentNode> children = c4DeploymentNode.getChildren();
        if (!children.isEmpty()) {
            children.forEach(child -> {
                DeploymentNode addedDeploymentNode = deploymentNode.addDeploymentNode(
                        child.getName(),
                        child.getDescription(),
                        child.getTechnology(),
                        child.getInstances()
                );

                addChildren(model, dataStructureModel, addedDeploymentNode, child);
            });
        }
    }
}
