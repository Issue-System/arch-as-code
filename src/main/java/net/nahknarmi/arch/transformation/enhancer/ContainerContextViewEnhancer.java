package net.nahknarmi.arch.transformation.enhancer;

import com.structurizr.Workspace;
import com.structurizr.model.Container;
import com.structurizr.model.Element;
import com.structurizr.model.Person;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.view.ContainerView;
import com.structurizr.view.ViewSet;
import net.nahknarmi.arch.domain.ArchitectureDataStructure;
import net.nahknarmi.arch.transformation.TransformationHelper;

public class ContainerContextViewEnhancer implements WorkspaceEnhancer {
    @Override
    public void enhance(Workspace workspace, ArchitectureDataStructure dataStructure) {
        ViewSet viewSet = workspace.getViews();

        dataStructure.getModel().getViews().getContainerView().getContainers().stream().forEach(c -> {
            String system = c.getSystem();
            SoftwareSystem softwareSystem = workspace.getModel().getSoftwareSystemWithName(system);

            Container container = softwareSystem.getContainerWithName(c.getName());
            // TODO: Fix missing description
            ContainerView context = viewSet.createContainerView(softwareSystem, c.getName(), "Missing Description");

            c.getRelationships().stream().forEach(r -> {
                String description = r.getDescription();
                Element fromElement = TransformationHelper.getElementWithName(workspace, r.getName());
                Element toElement = TransformationHelper.getElementWithName(workspace, r.getWith());

                // TODO: Currently only Person.uses & Container.uses interaction type supported
                addRelationshipToContext(context, fromElement, toElement, description);
            });

            context.addNearestNeighbours(container);
            context.setAutomaticLayout(true);
        });
    }


    private void addRelationshipToContext(ContainerView context, Element fromElement, Element toElement, String description) {
        // TODO: Clean up
        if (fromElement instanceof Person) {
            if (toElement instanceof SoftwareSystem) {
                ((Person) fromElement).uses((SoftwareSystem) toElement, description);
            } else {
                ((Person) fromElement).uses((Container) toElement, description);
            }
            context.add((Person) fromElement);
        } else if (fromElement instanceof Container) {
            if (toElement instanceof SoftwareSystem) {
                ((Container) fromElement).uses((SoftwareSystem) toElement, description);
            } else {
                ((Container) fromElement).uses((Container) toElement, description);
            }
            context.add((Container) fromElement);
        }
    }
}
