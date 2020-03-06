package net.nahknarmi.arch.transformation.enhancer;

import com.structurizr.Workspace;
import com.structurizr.model.DeploymentNode;
import com.structurizr.model.Model;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.view.DeploymentView;
import com.structurizr.view.ViewSet;
import lombok.NonNull;
import net.nahknarmi.arch.domain.ArchitectureDataStructure;
import net.nahknarmi.arch.domain.c4.C4DeploymentNode;
import net.nahknarmi.arch.domain.c4.C4Model;
import net.nahknarmi.arch.domain.c4.C4SoftwareSystem;
import net.nahknarmi.arch.domain.c4.view.C4DeploymentView;
import net.nahknarmi.arch.domain.c4.view.ModelMediator;

import java.util.List;

public class DeploymentViewEnhancer implements WorkspaceEnhancer {
    @Override
    public void enhance(Workspace workspace, ArchitectureDataStructure dataStructure) {
        @NonNull C4Model dataStructureModel = dataStructure.getModel();
        if (dataStructureModel.equals(C4Model.NONE)) {
            return;
        }

        @NonNull List<C4DeploymentView> views = getViews(dataStructure);

        views.forEach(c4DeploymentView -> {
            DeploymentView view = createView(workspace, dataStructureModel, c4DeploymentView);
            addDeploymentNode(workspace.getModel(), dataStructure, view, c4DeploymentView);
            view.setAutomaticLayout(true);
        });
    }

    private void addDeploymentNode(Model model, ArchitectureDataStructure dataStructure, DeploymentView view, C4DeploymentView c4DeploymentView) {
        c4DeploymentView.getReferences().forEach(ref -> {
            C4DeploymentNode c4DeploymentNode = (C4DeploymentNode) dataStructure.getModel().findEntityByReference(ref);
            DeploymentNode deploymentNode = (DeploymentNode) model.getElement(c4DeploymentNode.getId());
            view.add(deploymentNode);
            view.setEnvironment(c4DeploymentNode.getEnvironment());
        });
    }


    private DeploymentView createView(Workspace workspace, C4Model dataStructureModel, C4DeploymentView c4DeploymentView) {
        ViewSet viewSet = workspace.getViews();
        C4SoftwareSystem sys = (C4SoftwareSystem) dataStructureModel.findEntityByReference(c4DeploymentView.getSystem());
        SoftwareSystem softwareSystem = new ModelMediator(workspace.getModel()).softwareSystem(sys.getId());
        return viewSet.createDeploymentView(softwareSystem, c4DeploymentView.getKey(), c4DeploymentView.getDescription());
    }

    @NonNull
    private List<C4DeploymentView> getViews(ArchitectureDataStructure dataStructure) {
        return dataStructure.getViews().getDeploymentViews();
    }
}
