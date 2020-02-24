package net.nahknarmi.arch.transformation.enhancer;

import com.structurizr.Workspace;
import com.structurizr.model.Container;
import com.structurizr.view.ComponentView;
import com.structurizr.view.ViewSet;
import net.nahknarmi.arch.domain.ArchitectureDataStructure;
import net.nahknarmi.arch.domain.c4.C4Container;
import net.nahknarmi.arch.domain.c4.C4Model;
import net.nahknarmi.arch.domain.c4.Entity;
import net.nahknarmi.arch.domain.c4.view.C4ComponentView;
import net.nahknarmi.arch.domain.c4.view.ModelMediator;

import java.util.List;
import java.util.function.Consumer;

public class ComponentContextViewEnhancer extends BaseViewEnhancer<ComponentView, C4ComponentView> {
    @Override
    public List<C4ComponentView> getViews(ArchitectureDataStructure dataStructure) {
        return dataStructure.getViews().getComponentViews();
    }

    @Override
    public ComponentView createView(Workspace workspace, C4Model dataStructureModel, C4ComponentView componentView) {
        ViewSet viewSet = workspace.getViews();
        C4Container cont = componentView.getReferenced(dataStructureModel);
        Container container = new ModelMediator(workspace.getModel()).container(cont.getId());

        return viewSet.createComponentView(container, componentView.getKey(), componentView.getDescription());
    }

    public Consumer<Entity> addEntity(ModelMediator modelMediator, C4Model dataStructureModel, ComponentView view) {
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
                case component:
                    // TODO: Ensure this case is working properly
                    view.add(modelMediator.component(entity.getId()));
                    break;
                default:
                    throw new IllegalStateException("Unsupported type " + entity.getType());
            }
        };
    }
}
