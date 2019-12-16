package net.nahknarmi.arch.transformation.enhancer;

import com.structurizr.Workspace;
import com.structurizr.model.Model;
import com.structurizr.model.Person;
import com.structurizr.model.SoftwareSystem;
import net.nahknarmi.arch.domain.ArchitectureDataStructure;
import net.nahknarmi.arch.domain.c4.C4Model;
import net.nahknarmi.arch.domain.c4.C4Person;
import net.nahknarmi.arch.domain.c4.C4Relationship;
import net.nahknarmi.arch.domain.c4.C4SoftwareSystem;

import static java.util.Optional.ofNullable;
import static net.nahknarmi.arch.domain.c4.C4Model.NONE;

public class ModelEnhancer implements WorkspaceEnhancer {

    public void enhance(Workspace workspace, ArchitectureDataStructure dataStructure) {
        Model model = workspace.getModel();
        C4Model dataStructureModel = dataStructure.getModel();
        addPersons(model, dataStructureModel);
        addSystems(model, dataStructureModel);
        addRelationships(model, dataStructureModel);
    }

    private void addRelationships(Model model, C4Model dataStructureModel) {
        dataStructureModel.relationships().forEach(relationship -> {
            String fromName = relationship.getFrom().getName();
            String toName = relationship.getTo().getName();
            String description = relationship.getDescription();

            if (relationship.getFrom() instanceof C4Person) {
                processPerson(model, relationship, fromName, toName, description);
            } else if (relationship.getFrom() instanceof C4SoftwareSystem) {
                processSoftwareSystem(model, relationship, fromName, toName, description);
            } else {
                throw new IllegalStateException("Unsupported type - " + relationship.getFrom().getClass());
            }
        });
    }

    private void addSystems(Model model, C4Model dataStructureModel) {
        ofNullable(dataStructureModel)
                .orElse(NONE).getSystems()
                .forEach(s -> {
                    SoftwareSystem softwareSystem = model.addSoftwareSystem(s.getName(), s.getDescription());
                    s.getContainers().forEach(x -> softwareSystem.addContainer(x.getName(), x.getDescription(), x.getTechnology()));
                });
    }

    private void addPersons(Model model, C4Model dataStructureModel) {
        ofNullable(dataStructureModel)
                .orElse(NONE).getPersons()
                .forEach(p -> model.addPerson(p.getName(), p.getDescription()));
    }

    private void processPerson(Model model, C4Relationship r, String fromName, String toName, String description) {
        Person fromPerson = findPerson(model, fromName, fromName);

        switch (r.getRelationshipType()){
            case USES:
                personUses(model, toName, fromPerson, description);
                break;

            case INTERACTS_WITH: //person to person relationship
                personInteractsWith(model, fromName, toName, fromPerson);
                break;

            case DELIVERS: //person to person relationship
                personDelivers(model, fromName, toName, fromPerson);
                break;

            default:
                throw new IllegalStateException("Unexpected relationship type " + r.getRelationshipType());
        }
    }

    private void processSoftwareSystem(Model model, C4Relationship r, String fromName, String toName, String description) {
        SoftwareSystem fromSystem = findSoftwareSystem(model, fromName);

        switch (r.getRelationshipType()){
            case USES:
                systemUses(model, toName, fromSystem, description);
                break;

            case DELIVERS:
                systemDelivers(model, fromName, toName, fromSystem);
                break;

            default:
                throw new IllegalStateException("Unexpected relationship type " + r.getRelationshipType());
        }
    }

    private void systemDelivers(Model model, String fromName, String toName, SoftwareSystem fromSystem) {
        Person deliversTo = findPerson(model, fromName, toName);
        fromSystem.delivers(deliversTo, "delivers");
    }

    private void systemUses(Model model, String toName, SoftwareSystem fromSystem, String description) {
        SoftwareSystem toSystem = findSoftwareSystem(model, toName);
        fromSystem.uses(toSystem, description);
    }

    private void personUses(Model model, String toName, Person fromPerson, String description) {
        SoftwareSystem toSystem = findSoftwareSystem(model, toName);
        fromPerson.uses(toSystem, description);
    }

    private void personDelivers(Model model, String fromName, String toName, Person fromPerson) {
        Person deliversTo = findPerson(model, fromName, toName);
        fromPerson.delivers(deliversTo, "delivers");
    }

    private void personInteractsWith(Model model, String fromName, String toName, Person fromPerson) {
        Person interactsWith = findPerson(model, fromName, toName);
        fromPerson.interactsWith(interactsWith, "interacts with");
    }

    private SoftwareSystem findSoftwareSystem(Model model, String toName) {
        return ofNullable(model.getSoftwareSystemWithName(toName))
                .orElseThrow(() -> new IllegalStateException("System with name " + toName + " not found."));
    }

    private Person findPerson(Model model, String fromName, String toName) {
        return ofNullable(model.getPersonWithName(toName))
                .orElseThrow(() -> new IllegalStateException("Person with name " + fromName + " not found."));
    }

}
