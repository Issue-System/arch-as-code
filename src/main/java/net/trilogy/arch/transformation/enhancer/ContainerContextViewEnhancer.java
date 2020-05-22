package net.trilogy.arch.transformation.enhancer;

import com.structurizr.Workspace;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.view.ContainerView;
import com.structurizr.view.ViewSet;
import net.trilogy.arch.domain.ArchitectureDataStructure;
import net.trilogy.arch.domain.c4.C4Model;
import net.trilogy.arch.domain.c4.C4SoftwareSystem;
import net.trilogy.arch.domain.c4.Entity;
import net.trilogy.arch.domain.c4.view.C4ContainerView;
import net.trilogy.arch.domain.c4.view.ModelMediator;

import java.util.List;
import java.util.function.Consumer;

public class ContainerContextViewEnhancer extends BaseViewEnhancer<ContainerView, C4ContainerView> {

    @Override
    public List<C4ContainerView> getViews(ArchitectureDataStructure dataStructure) {
        return dataStructure.getViews().getContainerViews();
    }

    @Override
    public ContainerView createView(Workspace workspace, C4Model dataStructureModel, C4ContainerView containerView) {
        ViewSet viewSet = workspace.getViews();
        C4SoftwareSystem sys = containerView.getReferenced(dataStructureModel);
        SoftwareSystem softwareSystem = new ModelMediator(workspace.getModel()).softwareSystem(sys.getId());

        return viewSet.createContainerView(softwareSystem, containerView.getKey(), containerView.getDescription());
    }

    public Consumer<Entity> addEntity(ModelMediator modelMediator, C4Model dataStructureModel, ContainerView view) {
        return addEntityWithRelationships(modelMediator, dataStructureModel, view);
    }

    public Consumer<Entity> addEntityWithRelationships(ModelMediator modelMediator, C4Model dataStructureModel, ContainerView view) {
        return entity -> {
            switch (entity.getType()) {
                case person:
                    view.add(modelMediator.person(entity.getId()));
                    break;
                case system:
                    view.add(modelMediator.softwareSystem(entity.getId()));
                    break;
                case container:
                    view.add(modelMediator.container(entity.getId()));
                    break;
                default:
                    throw new IllegalStateException("Unsupported type " + entity.getType());
            }
        };
    }
}
