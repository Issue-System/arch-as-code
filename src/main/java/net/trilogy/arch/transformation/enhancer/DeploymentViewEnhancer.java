package net.trilogy.arch.transformation.enhancer;

import com.structurizr.Workspace;
import com.structurizr.model.DeploymentNode;
import com.structurizr.model.Model;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.view.AutomaticLayout;
import com.structurizr.view.DeploymentView;
import com.structurizr.view.ViewSet;
import lombok.NonNull;
import net.trilogy.arch.domain.ArchitectureDataStructure;
import net.trilogy.arch.domain.c4.C4DeploymentNode;
import net.trilogy.arch.domain.c4.C4Model;
import net.trilogy.arch.domain.c4.C4SoftwareSystem;
import net.trilogy.arch.domain.c4.view.C4DeploymentView;
import net.trilogy.arch.domain.c4.view.ModelMediator;

import java.util.Set;

public class DeploymentViewEnhancer implements WorkspaceEnhancer {
    @Override
    public void enhance(Workspace workspace, ArchitectureDataStructure dataStructure) {
        @NonNull C4Model dataStructureModel = dataStructure.getModel();
        if (dataStructureModel.equals(C4Model.NONE)) {
            return;
        }

        @NonNull Set<C4DeploymentView> views = getViews(dataStructure);

        views.forEach(c4DeploymentView -> {
            DeploymentView view = createView(workspace, dataStructureModel, c4DeploymentView);
            addDeploymentNode(workspace.getModel(), dataStructure, view, c4DeploymentView);
            view.setAutomaticLayout(AutomaticLayout.RankDirection.TopBottom, 100, 100, 50, true);
        });
    }

    private void addDeploymentNode(Model model, ArchitectureDataStructure dataStructure, DeploymentView view, C4DeploymentView c4DeploymentView) {
        c4DeploymentView.getElements().forEach(ref -> {
            C4DeploymentNode c4DeploymentNode = (C4DeploymentNode) dataStructure.getModel().findEntityByReference(ref);
            DeploymentNode deploymentNode = (DeploymentNode) model.getElement(c4DeploymentNode.getId());
            view.add(deploymentNode);
            view.setEnvironment(c4DeploymentNode.getEnvironment());
        });
    }

    private DeploymentView createView(Workspace workspace, C4Model dataStructureModel, C4DeploymentView c4DeploymentView) {
        ViewSet viewSet = workspace.getViews();

        if (c4DeploymentView.getSystem() == null) {
            return viewSet.createDeploymentView(c4DeploymentView.getKey(), c4DeploymentView.getDescription());
        }

        C4SoftwareSystem sys = (C4SoftwareSystem) dataStructureModel.findEntityByReference(c4DeploymentView.getSystem());
        SoftwareSystem softwareSystem = new ModelMediator(workspace.getModel()).softwareSystem(sys.getId());
        return viewSet.createDeploymentView(softwareSystem, c4DeploymentView.getKey(), c4DeploymentView.getDescription());
    }

    @NonNull
    private Set<C4DeploymentView> getViews(ArchitectureDataStructure dataStructure) {
        return dataStructure.getViews().getDeploymentViews();
    }
}
