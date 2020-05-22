package net.trilogy.arch.domain.c4.view;

import com.structurizr.model.*;
import net.trilogy.arch.domain.c4.*;
import net.trilogy.arch.generator.FunctionalIdGenerator;
import net.trilogy.arch.transformation.DeploymentNodeTransformer;
import net.trilogy.arch.transformation.LocationTransformer;

public class ModelMediator {
    private final Model model;
    private final FunctionalIdGenerator idGenerator;

    public ModelMediator(Model model, FunctionalIdGenerator idGenerator) {
        this.model = model;
        this.idGenerator = idGenerator;
    }

    public ModelMediator(Model model) {
        this.model = model;
        this.idGenerator = new FunctionalIdGenerator();
    }

    public Person person(String id) {
        return (Person) model.getElement(id);
    }

    public Person person(C4Path path) {
        String id = path.getId();
        return (Person) model.getElement(id);
    }

    public SoftwareSystem softwareSystem(String id) {
        return (SoftwareSystem) model.getElement(id);
    }

    public SoftwareSystem softwareSystem(C4Path path) {
        String id = path.getId();
        return (SoftwareSystem) model.getElement(id);
    }

    public Container container(String id) {
        return (Container) model.getElement(id);
    }

    public Container container(C4Path path) {
        String id = path.getId();
        return (Container) model.getElement(id);
    }

    public Component component(String id) {
        return (Component) model.getElement(id);
    }

    public Component component(C4Path path) {
        String id = path.getId();
        return (Component) model.getElement(id);
    }


    public SoftwareSystem addSoftwareSystem(C4SoftwareSystem softwareSystem) {
        Location location = LocationTransformer.c4LocationToLocation(softwareSystem.getLocation());
        idGenerator.setNext(softwareSystem.getId());
        SoftwareSystem result = this.model.addSoftwareSystem(location, softwareSystem.getName(), softwareSystem.getDescription());
        result.addTags(getTags(softwareSystem));
        return result;
    }

    public Person addPerson(C4Person person) {
        Location location = LocationTransformer.c4LocationToLocation(person.getLocation());
        idGenerator.setNext(person.getId());
        Person result = model.addPerson(location, person.getName(), person.getDescription());
        result.addTags(getTags(person));
        return result;
    }

    public Container addContainer(C4SoftwareSystem system, C4Container container) {
        SoftwareSystem softwareSystem = new ModelMediator(model).softwareSystem(system.getId());
        idGenerator.setNext(container.getId());
        Container result = softwareSystem.addContainer(container.getName(), container.getDescription(), container.getTechnology());
        result.addTags(getTags(container));
        result.setUrl(container.getUrl());
        return result;
    }

    public Component addComponent(C4Container c4Container, C4Component component) {
        Container container = new ModelMediator(model).container(c4Container.getId());
        idGenerator.setNext(component.getId());
        Component result = container.addComponent(component.getName(), component.getDescription(), component.getTechnology());
        result.addTags(getTags(component));
        result.setUrl(component.getUrl());

        // TODO [AAC-101][2] Send source code mappings as property
        
        return result;
    }

    public DeploymentNode addDeploymentNode(C4Model dataStructureModel, C4DeploymentNode c4DeploymentNode) {
        return DeploymentNodeTransformer.addDeploymentNodeFromC4ToModel(c4DeploymentNode, dataStructureModel, model, idGenerator);
    }

    private String[] getTags(HasTag t) {
        return t.getTags().stream().map(C4Tag::getTag).toArray(String[]::new);
    }
}
