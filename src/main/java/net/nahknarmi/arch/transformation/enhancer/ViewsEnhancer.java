package net.nahknarmi.arch.transformation.enhancer;

import com.structurizr.Workspace;
import com.structurizr.model.Tags;
import com.structurizr.view.*;
import net.nahknarmi.arch.domain.ArchitectureDataStructure;

public class ViewsEnhancer implements WorkspaceEnhancer {
    @Override
    public void enhance(Workspace workspace, ArchitectureDataStructure dataStructure) {
        ViewSet viewSet = workspace.getViews();

        workspace.getModel().getSoftwareSystems().stream().findFirst().ifPresent(ss -> {
            SystemContextView context = viewSet.createSystemContextView(ss, ss.getName() + "-context", ss.getName() + " Diagram");
            context.addAllSoftwareSystems();
            context.addAllPeople();
            context.setAutomaticLayout(true);

            Styles styles = viewSet.getConfiguration().getStyles();
            styles.addElementStyle(Tags.SOFTWARE_SYSTEM).background("#1168bd").color("#ffffff");
            styles.addElementStyle(Tags.PERSON).background("#08427b").color("#ffffff").shape(Shape.Person);

            context.setAutomaticLayout(AutomaticLayout.RankDirection.TopBottom, 300, 600, 200, false);
        });
    }
}
