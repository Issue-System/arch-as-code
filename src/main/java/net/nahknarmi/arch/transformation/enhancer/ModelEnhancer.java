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
//        addRelationships(model, dataStructureModel);
    }

//    private void addRelationships(Model model, C4Model dataStructureModel) {
//        dataStructureModel.relationships().forEach(relationship -> {
//            String fromName = relationship.getFrom().getName();
//            String toName = relationship.getTo().getName();
//            String description = relationship.getDescription();
//
//            if (relationship.getFrom() instanceof C4Person) {
//                processPerson(model, relationship, fromName, toName, description);
//            } else if (relationship.getFrom() instanceof C4SoftwareSystem) {
//                processSoftwareSystem(model, relationship, fromName, toName, description);
//            } else {
//                throw new IllegalStateException("Unsupported type - " + relationship.getFrom().getClass());
//            }
//        });
//    }

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
//        s.getContainers().forEach(c -> {
//            addContainerRelationships(model, softwareSystem, c);
//        });
    }

//    private void addContainerRelationships(Model model, SoftwareSystem system, C4Container c) {
//        Container container = system.getContainerWithName(c.getName());
//        c.getRelationships().forEach(r -> {
//            processContainer(model, system, container, r);
//        });
//    }

    private void addContainer(SoftwareSystem softwareSystem, C4Container c) {
        softwareSystem.addContainer(c.getName(), c.getDescription(), c.getTechnology());
    }

    private void addPersons(Model model, C4Model dataStructureModel) {
        ofNullable(dataStructureModel)
                .orElse(NONE)
                .getPersons()
                .forEach(p -> model.addPerson(p.getName(), p.getDescription()));
    }

//    private void processPerson(Model model, C4Relationship r, String fromName, String toName, String description) {
//        Person fromPerson = findPerson(model, fromName, fromName);
//
//        switch (r.getRelationshipType()) {
//            case USES:
//                personUses(model, toName, fromPerson, description);
//                break;
//
//            case INTERACTS_WITH: //person to person relationship
//                personInteractsWith(model, fromName, toName, fromPerson);
//                break;
//
//            case DELIVERS: //person to person relationship
//                personDelivers(model, fromName, toName, fromPerson);
//                break;
//
//            default:
//                throw new IllegalStateException("Unexpected relationship type " + r.getRelationshipType());
//        }
//    }
//
//    private void processSoftwareSystem(Model model, C4Relationship r, String fromName, String toName, String description) {
//        SoftwareSystem fromSystem = findSoftwareSystem(model, fromName);
//
//        switch (r.getRelationshipType()) {
//            case USES:
//                systemUses(model, toName, fromSystem, description);
//                break;
//
//            case DELIVERS:
//                systemDelivers(model, fromName, toName, fromSystem);
//                break;
//
//            default:
//                throw new IllegalStateException("Unexpected relationship type " + r.getRelationshipType());
//        }
//    }
//
//    private void processContainer(Model model, SoftwareSystem parent, Container container, RelationshipPair r) {
//        Element found = findElementWithName(model, r.getWith());
//
//        switch (RelationshipType.valueOf(r.getName())) {
//            case USES:
//                if (found instanceof Container) {
//                    Container destination = (Container) found;
//                    container.uses(destination, r.getDescription());
//                } else {
//                    SoftwareSystem destination = (SoftwareSystem) found;
//                    container.uses(destination, r.getDescription());
//                }
//                break;
//            case DELIVERS:
//                if (found instanceof Person) {
//                    Person destination = (Person) found;
//                    container.delivers(destination, r.getDescription());
//                } else {
//                    throw new IllegalStateException("Container must deliver to a Person, found: " + found.getClass());
//                }
//                break;
//            default:
//                throw new IllegalStateException("Unexpected relationship type: " + r.getName());
//        }
//    }
//
//    private void systemDelivers(Model model, String fromName, String toName, SoftwareSystem fromSystem) {
//        Person deliversTo = findPerson(model, fromName, toName);
//        fromSystem.delivers(deliversTo, "delivers");
//    }
//
//    private void systemUses(Model model, String toName, SoftwareSystem fromSystem, String description) {
//        SoftwareSystem toSystem = findSoftwareSystem(model, toName);
//        fromSystem.uses(toSystem, description);
//    }
//
//    private void personUses(Model model, String toName, Person fromPerson, String description) {
//        SoftwareSystem toSystem = findSoftwareSystem(model, toName);
//        fromPerson.uses(toSystem, description);
//    }
//
//    private void personDelivers(Model model, String fromName, String toName, Person fromPerson) {
//        Person deliversTo = findPerson(model, fromName, toName);
//        fromPerson.delivers(deliversTo, "delivers");
//    }
//
//    private void personInteractsWith(Model model, String fromName, String toName, Person fromPerson) {
//        Person interactsWith = findPerson(model, fromName, toName);
//        fromPerson.interactsWith(interactsWith, "interacts with");
//    }
//
//    private SoftwareSystem findSoftwareSystem(Model model, String toName) {
//        return ofNullable(model.getSoftwareSystemWithName(toName))
//                .orElseThrow(() -> new IllegalStateException("System with name " + toName + " not found."));
//    }
//
//    private Element findElementWithName(Model model, String elementName) {
//        SoftwareSystem candidateSystem = model.getSoftwareSystemWithName(elementName);
//        if (candidateSystem != null) {
//            return candidateSystem;
//        }
//
//        Person candidatePerson = model.getPersonWithName(elementName);
//        if (candidatePerson != null) {
//            return candidatePerson;
//        }
//
//        Container candidateContainer = model
//                .getSoftwareSystems()
//                .stream()
//                .map(s -> s.getContainerWithName(elementName))
//                .filter(Objects::nonNull)
//                .findFirst()
//                .get();
//
//        return candidateContainer;
//    }
//
//    private Person findPerson(Model model, String fromName, String toName) {
//        return ofNullable(model.getPersonWithName(toName))
//                .orElseThrow(() -> new IllegalStateException("Person with name " + fromName + " not found."));
//    }

}
