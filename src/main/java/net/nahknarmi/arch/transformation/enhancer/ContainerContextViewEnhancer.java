package net.nahknarmi.arch.transformation.enhancer;

import com.structurizr.Workspace;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.view.ContainerView;
import com.structurizr.view.ViewSet;
import net.nahknarmi.arch.domain.ArchitectureDataStructure;
import net.nahknarmi.arch.domain.c4.*;
import net.nahknarmi.arch.domain.c4.view.C4ContainerView;
import net.nahknarmi.arch.domain.c4.view.ModelMediator;

import java.util.List;
import java.util.function.Consumer;

public class ContainerContextViewEnhancer extends BaseViewEnhancer<ContainerView, C4ContainerView> {

    @Override
    public List<C4ContainerView> getViews(ArchitectureDataStructure dataStructure) {
        return dataStructure.getViews().getContainerViews();
    }

    @Override
    public ContainerView createView(Workspace workspace, C4Model dataStructureModel, C4ContainerView c) {
        ViewSet viewSet = workspace.getViews();

        C4SoftwareSystem sysWithId = (C4SoftwareSystem) dataStructureModel.findByPath(c.getSystemPath());
        SoftwareSystem softwareSystem = new ModelMediator(workspace.getModel()).softwareSystem(sysWithId.getPath());
        return viewSet.createContainerView(softwareSystem, c.getKey(), c.getDescription());
    }

    public Consumer<C4Path> addEntity(ModelMediator modelMediator, C4Model dataStructureModel, ContainerView view) {
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
                default:
                    throw new IllegalStateException("Unsupported type " + entityPath.type());
            }
        };
    }
}
