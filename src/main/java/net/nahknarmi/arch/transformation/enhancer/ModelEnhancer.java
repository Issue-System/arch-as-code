package net.nahknarmi.arch.transformation.enhancer;

import com.structurizr.Workspace;
import com.structurizr.model.*;
import net.nahknarmi.arch.domain.ArchitectureDataStructure;
import net.nahknarmi.arch.domain.c4.*;

import java.util.Objects;

import static java.util.Optional.ofNullable;
import static net.nahknarmi.arch.domain.c4.C4Model.NONE;

public class ModelEnhancer implements WorkspaceEnhancer {

    public void enhance(Workspace workspace, ArchitectureDataStructure dataStructure) {
        Model model = workspace.getModel();
        C4Model dataStructureModel = dataStructure.getModel();
        addPersons(model, dataStructureModel);
        addSystems(model, dataStructureModel);
    }

    private void addSystems(Model model, C4Model dataStructureModel) {
        ofNullable(dataStructureModel)
                .orElse(NONE).getSystems()
                .forEach(s -> {
                    addSystem(model, s);
                });
    }

    private void addSystem(Model model, C4SoftwareSystem s) {
        SoftwareSystem softwareSystem = model.addSoftwareSystem(s.getName(), s.getDescription());
        s.getContainers().forEach(c -> {
            addContainer(softwareSystem, c);
        });
    }

    private void addContainer(SoftwareSystem softwareSystem, C4Container c) {
        softwareSystem.addContainer(c.getName(), c.getDescription(), c.getTechnology());
    }

    private void addPersons(Model model, C4Model dataStructureModel) {
        ofNullable(dataStructureModel)
                .orElse(NONE)
                .getPersons()
                .forEach(p -> model.addPerson(p.getName(), p.getDescription()));
    }
}
