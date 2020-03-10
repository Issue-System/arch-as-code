package net.trilogy.arch.transformation.enhancer;

import com.structurizr.Workspace;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.view.SystemContextView;
import com.structurizr.view.ViewSet;
import net.trilogy.arch.domain.ArchitectureDataStructure;
import net.trilogy.arch.domain.c4.C4Model;
import net.trilogy.arch.domain.c4.C4SoftwareSystem;
import net.trilogy.arch.domain.c4.Entity;
import net.trilogy.arch.domain.c4.view.C4SystemView;
import net.trilogy.arch.domain.c4.view.ModelMediator;

import java.util.List;
import java.util.function.Consumer;

public class SystemContextViewEnhancer extends BaseViewEnhancer<SystemContextView, C4SystemView> {

    @Override
    public List<C4SystemView> getViews(ArchitectureDataStructure dataStructure) {
        return dataStructure.getViews().getSystemViews();
    }

    @Override
    public SystemContextView createView(Workspace workspace, C4Model dataStructureModel, C4SystemView systemView) {
        ViewSet viewSet = workspace.getViews();
        C4SoftwareSystem sys = systemView.getReferenced(dataStructureModel);
        SoftwareSystem softwareSystem = new ModelMediator(workspace.getModel()).softwareSystem(sys.getId());

        return viewSet.createSystemContextView(softwareSystem, systemView.getKey(), systemView.getDescription());
    }

    public Consumer<Entity> addEntity(ModelMediator modelMediator, C4Model dataStructureModel, SystemContextView view) {
        return entity -> {
            switch (entity.getType()) {
                case person:
                    view.add(modelMediator.person(entity.getId()));
                    break;
                case system:
                    view.add(modelMediator.softwareSystem(entity.getId()));
                    break;
                default:
                    throw new IllegalStateException("Unsupported type " + entity.getType());
            }
        };
    }
}
