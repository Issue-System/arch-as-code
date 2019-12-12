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

public class ModelEnhancer implements WorkspaceEnhancer {

    public void enhance(Workspace workspace, ArchitectureDataStructure dataStructure) {
        Model model = workspace.getModel();
        C4Model dataStructureModel = dataStructure.getModel();
        addPersons(model, dataStructureModel);
        addSystems(model, dataStructureModel);
        addRelationships(model, dataStructureModel);
    }

    private void addRelationships(Model model, C4Model dataStructureModel) {
        dataStructureModel.relationships().forEach(r -> {
            String fromName = r.getFrom().getName();
            String toName = r.getTo().getName();

            if (r.getFrom() instanceof C4Person) {
                processPerson(model, r, fromName, toName);
            } else if (r.getFrom() instanceof C4SoftwareSystem) {
                processSoftwareSystem(model, r, fromName, toName);
            } else {
                throw new IllegalStateException("Unsupported type - " + r.getFrom().getClass());
            }
        });
    }

    private void addSystems(Model model, C4Model dataStructureModel) {
        dataStructureModel.getSystems().forEach(s -> model.addSoftwareSystem(s.getName(), s.getDescription()));
    }

    private void addPersons(Model model, C4Model dataStructureModel) {
        dataStructureModel.getPersons().forEach(p -> model.addPerson(p.getName(), p.getDescription()));
    }

    private void processPerson(Model model, C4Relationship r, String fromName, String toName) {
        Person fromPerson = findPerson(model, fromName, fromName);

        switch (r.getRelationshipType()){
            case USES:
                personUses(model, toName, fromPerson);
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

    private void processSoftwareSystem(Model model, C4Relationship r, String fromName, String toName) {
        SoftwareSystem fromSystem = findSoftwareSystem(model, fromName);

        switch (r.getRelationshipType()){
            case USES:
                systemUses(model, toName, fromSystem);
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

    private void systemUses(Model model, String toName, SoftwareSystem fromSystem) {
        SoftwareSystem toSystem = findSoftwareSystem(model, toName);
        fromSystem.uses(toSystem, "uses");
    }

    private void personUses(Model model, String toName, Person fromPerson) {
        SoftwareSystem toSystem = findSoftwareSystem(model, toName);
        fromPerson.uses(toSystem, "uses");
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
