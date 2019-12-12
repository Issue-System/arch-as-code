package net.nahknarmi.arch.transformation.enhancer;

import com.structurizr.Workspace;
import com.structurizr.view.AutomaticLayout;
import com.structurizr.view.SystemContextView;
import com.structurizr.view.ViewSet;
import net.nahknarmi.arch.model.ArchitectureDataStructure;

public class ViewsEnhancer implements WorkspaceEnhancer {
    @Override
    public void enhance(Workspace workspace, ArchitectureDataStructure dataStructure) {
        ViewSet viewSet = workspace.getViews();

        workspace.getModel().getSoftwareSystems().stream().findFirst().ifPresent(ss -> {
            SystemContextView context = viewSet.createSystemContextView(ss, ss.getName() + "-context", ss.getName() + " Diagram");
            context.addAllSoftwareSystems();
            context.addAllPeople();
            context.setAutomaticLayout(true);
            context.setAutomaticLayout(AutomaticLayout.RankDirection.LeftRight, 300, 600, 200, false);
        });
    }
}
