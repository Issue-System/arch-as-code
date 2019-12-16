package net.nahknarmi.arch.transformation.enhancer;

import com.structurizr.Workspace;
import com.structurizr.view.AutomaticLayout;
import com.structurizr.view.ContainerView;
import com.structurizr.view.ViewSet;
import net.nahknarmi.arch.domain.ArchitectureDataStructure;

public class ContainerContextViewEnhancer implements WorkspaceEnhancer {
    @Override
    public void enhance(Workspace workspace, ArchitectureDataStructure dataStructure) {
        ViewSet viewSet = workspace.getViews();

        workspace.getModel().getSoftwareSystems().forEach(ss -> {
            ContainerView context = viewSet.createContainerView(ss, ss.getName() + "-container-context", ss.getName() + " Container Diagram");
            context.addAllSoftwareSystems();
            context.addAllPeople();
            context.addAllContainers();
            context.setAutomaticLayout(true);

            context.setAutomaticLayout(AutomaticLayout.RankDirection.TopBottom, 300, 600, 200, false);
        });
    }
}
