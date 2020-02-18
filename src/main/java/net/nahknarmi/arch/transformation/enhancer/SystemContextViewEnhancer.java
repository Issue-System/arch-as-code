package net.nahknarmi.arch.transformation.enhancer;

import com.structurizr.Workspace;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.view.SystemContextView;
import com.structurizr.view.ViewSet;
import net.nahknarmi.arch.domain.ArchitectureDataStructure;
import net.nahknarmi.arch.domain.c4.C4Model;
import net.nahknarmi.arch.domain.c4.C4Path;
import net.nahknarmi.arch.domain.c4.C4Person;
import net.nahknarmi.arch.domain.c4.C4SoftwareSystem;
import net.nahknarmi.arch.domain.c4.view.C4SystemView;
import net.nahknarmi.arch.domain.c4.view.ModelMediator;

import java.util.List;
import java.util.function.Consumer;

public class SystemContextViewEnhancer extends BaseViewEnhancer<SystemContextView, C4SystemView> {

    @Override
    public List<C4SystemView> getViews(ArchitectureDataStructure dataStructure) {
        return dataStructure.getViews().getSystemViews();
    }

    @Override
    public SystemContextView createView(Workspace workspace, C4Model dataStructureModel, C4SystemView view) {
        ViewSet viewSet = workspace.getViews();
        C4SoftwareSystem sysWithId = (C4SoftwareSystem) dataStructureModel.findByPath(view.getSystemPath());
        SoftwareSystem softwareSystem = new ModelMediator(workspace.getModel()).softwareSystem(sysWithId.getPath());
        return viewSet.createSystemContextView(softwareSystem, view.getKey(), view.getDescription());
    }

    public Consumer<C4Path> addEntity(ModelMediator modelMediator, C4Model dataStructureModel, SystemContextView view) {
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
                default:
                    throw new IllegalStateException("Unsupported type " + entityPath.type());
            }
        };
    }
}
