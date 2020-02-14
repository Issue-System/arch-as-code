package net.nahknarmi.arch.transformation.enhancer;

import com.structurizr.Workspace;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.view.ComponentView;
import com.structurizr.view.ViewSet;
import net.nahknarmi.arch.domain.ArchitectureDataStructure;
import net.nahknarmi.arch.domain.c4.*;
import net.nahknarmi.arch.domain.c4.view.C4ComponentView;
import net.nahknarmi.arch.domain.c4.view.ModelMediator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Consumer;

public class ComponentContextViewEnhancer extends BaseViewEnhancer<ComponentView, C4ComponentView> {
    private static Logger log = LoggerFactory.getLogger(ComponentContextViewEnhancer.class);

    @Override
    public List<C4ComponentView> getViews(ArchitectureDataStructure dataStructure) {
        return dataStructure.getViews().getComponentViews();
    }

    @Override
    public ComponentView createView(Workspace workspace, C4Model dataStructureModel, C4ComponentView componentView) {
        ViewSet viewSet = workspace.getViews();
        C4Container contWithId = (C4Container) dataStructureModel.findByPath(componentView.getContainerPath());
        Container container = new ModelMediator(workspace.getModel()).container(contWithId.getPath());

        return viewSet.createComponentView(container, componentView.getKey(), componentView.getDescription());
    }

    public Consumer<C4Path> addEntity(ModelMediator modelMediator, C4Model dataStructureModel, ComponentView view) {
        return entityPath -> {

            switch (entityPath.type()) {
                case person:
                    C4Person personWithId = (C4Person) dataStructureModel.findByPath(entityPath.personPath());
                    view.add(modelMediator.person(personWithId.getPath()));
                    break;
                case system:
                    C4SoftwareSystem sysWithId = (C4SoftwareSystem) dataStructureModel.findByPath(entityPath.systemPath());
                    view.add(modelMediator.softwareSystem(sysWithId.getPath()));
                    break;
                case container:
                    C4Container contWithId = (C4Container) dataStructureModel.findByPath(entityPath.containerPath());
                    view.add(modelMediator.container(contWithId.getPath()));
                    break;
                case component:
                    Container container = view.getContainer();
                    C4Component compWithId = (C4Component) dataStructureModel.findByPath(entityPath.componentPath());

                    Component component = modelMediator.component(compWithId.getPath());

                    //TODO: Find out why there are cases where input is broken
                    if (component.getContainer().equals(container)) {
                        view.add(component);
                    } else {
                        log.warn("Only components belonging to " + container + " can be added to view (" + component + " not added.).");
                    }
                    break;
                default:
                    throw new IllegalStateException("Unsupported type " + entityPath.type());
            }
        };
    }
}
