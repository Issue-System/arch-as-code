package net.nahknarmi.arch.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.structurizr.Workspace;
import com.structurizr.model.*;
import com.structurizr.util.WorkspaceUtils;
import com.structurizr.view.StaticView;
import net.nahknarmi.arch.domain.ArchitectureDataStructure;
import net.nahknarmi.arch.domain.c4.*;
import net.nahknarmi.arch.domain.c4.view.C4ComponentView;
import net.nahknarmi.arch.domain.c4.view.C4ContainerView;
import net.nahknarmi.arch.domain.c4.view.C4SystemView;
import net.nahknarmi.arch.domain.c4.view.C4View;
import picocli.CommandLine;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import static java.util.stream.Collectors.toList;
import static net.nahknarmi.arch.domain.c4.C4Path.buildPath;

@CommandLine.Command(name = "import", description = "Imports existing Struturizr workspace")
public class ImportCommand implements Callable<Integer> {

    @CommandLine.Parameters(index = "0", paramLabel = "EXPORTED_WORKSPACE", description = "Exported structurizr workspace location.", defaultValue = "./")
    private File exportedWorkspacePath;

    // Only for testing purposes
    public ImportCommand(File exportedWorkspacePath) {
        this.exportedWorkspacePath = exportedWorkspacePath;
    }

    @Override
    public Integer call() throws Exception {

        Workspace workspace = WorkspaceUtils.loadWorkspaceFromJson(exportedWorkspacePath);


        ArchitectureDataStructure architectureDataStructure = new ArchitectureDataStructure();
        architectureDataStructure.setModel(new C4Model());
        mapMetadata(workspace, architectureDataStructure);
        mapPeople(workspace, architectureDataStructure);
        mapSoftwareSystems(workspace, architectureDataStructure);
        mapContainers(workspace, architectureDataStructure);
        mapComponents(workspace, architectureDataStructure);
        mapSystemViews(workspace, architectureDataStructure);
        mapContainerViews(workspace, architectureDataStructure);
        mapComponentViews(workspace, architectureDataStructure);

        File tempFile = File.createTempFile("arch-as-code", "yml");
        new ObjectMapper(new YAMLFactory()).writeValue(tempFile, architectureDataStructure);


        return new PublishCommand(tempFile.getParentFile(), tempFile.getName()).call();
    }

    private void mapSystemViews(Workspace workspace, ArchitectureDataStructure architectureDataStructure) {
        List<C4SystemView> systemViews = workspace.getViews()
                .getSystemContextViews()
                .stream()
                .map(x -> {
                    SoftwareSystem softwareSystem = x.getSoftwareSystem();
                    C4Path c4Path = buildPath(softwareSystem);
                    C4SystemView c4SystemView = new C4SystemView(c4Path);
                    mapCommonViewAttributes(x, c4SystemView);

                    return c4SystemView;
                })
                .collect(toList());
        architectureDataStructure.getViews().setSystemViews(systemViews);
    }

    private void mapContainerViews(Workspace workspace, ArchitectureDataStructure architectureDataStructure) {
        List<C4ContainerView> result = workspace.getViews()
                .getContainerViews()
                .stream()
                .map(x -> {
                    SoftwareSystem softwareSystem = x.getSoftwareSystem();
                    C4Path c4Path = buildPath(softwareSystem);
                    C4ContainerView view = new C4ContainerView(c4Path);
                    mapCommonViewAttributes(x, view);

                    return view;
                })
                .collect(toList());
        architectureDataStructure.getViews().setContainerViews(result);
    }

    private void mapComponentViews(Workspace workspace, ArchitectureDataStructure architectureDataStructure) {
        List<C4ComponentView> result = workspace.getViews()
                .getComponentViews()
                .stream()
                .map(x -> {
                    Container container = x.getContainer();
                    C4Path c4Path = buildPath(container);
                    C4ComponentView view = new C4ComponentView(c4Path);
                    mapCommonViewAttributes(x, view);

                    return view;
                })
                .collect(toList());
        architectureDataStructure.getViews().setComponentViews(result);
    }

    private void mapCommonViewAttributes(StaticView x, C4View view) {
        view.setName(x.getName());
        view.setDescription(x.getDescription());
        List<C4Path> elements = x.getElements().stream().map(y -> buildPath(y.getElement())).collect(toList());
        view.setEntities(elements);
    }

    private void mapComponents(Workspace workspace, ArchitectureDataStructure architectureDataStructure) {
        List<C4Component> c4Components = workspace.getModel()
                .getSoftwareSystems()
                .stream()
                .flatMap(s -> s.getContainers()
                        .stream()
                        .flatMap(c -> c.getComponents()
                                .stream()
                                .map(co -> {
                                    C4Path c4Path = buildPath(co);

                                    List<C4Tag> tags = convertTags(co.getTags());
                                    List<C4Relationship> relationships = mapRelationships(co, co.getRelationships());
                                    return new C4Component(c4Path, co.getTechnology(), co.getDescription(), tags, relationships);
                                })

                        )

                ).collect(toList());
        architectureDataStructure.getModel().setComponents(c4Components);
    }

    private void mapContainers(Workspace workspace, ArchitectureDataStructure architectureDataStructure) {
        //containers
        List<C4Container> containers = workspace.getModel()
                .getSoftwareSystems()
                .stream()
                .flatMap(s -> s.getContainers().stream().map(c -> {
                    List<C4Relationship> relationships = mapRelationships(c, c.getRelationships());
                    List<C4Tag> tags = convertTags(c.getTags());

                    C4Path path = buildPath(c);
                    return new C4Container(path, c.getTechnology(), c.getDescription(), tags, relationships);
                }))
                .collect(toList());
        architectureDataStructure.getModel().setContainers(containers);
    }

    private void mapSoftwareSystems(Workspace workspace, ArchitectureDataStructure architectureDataStructure) {
        List<C4SoftwareSystem> softwareSystems = workspace.getModel()
                .getSoftwareSystems()
                .stream()
                .map(x -> {
                    List<C4Relationship> relationships = mapRelationships(x, x.getRelationships());
                    List<C4Tag> tags = convertTags(x.getTags());
                    C4Path path = buildPath(x);
                    return new C4SoftwareSystem(path, x.getDescription(), convertLocation(x.getLocation()), tags, relationships);
                })
                .collect(toList());
        architectureDataStructure.getModel().setSystems(softwareSystems);
    }

    private void mapPeople(Workspace workspace, ArchitectureDataStructure architectureDataStructure) {
        //map people
        List<C4Person> people = workspace.getModel()
                .getPeople()
                .stream()
                .map(x -> {
                    List<C4Relationship> relationships = mapRelationships(x, x.getRelationships());
                    List<C4Tag> tags = convertTags(x.getTags());
                    C4Path path = buildPath(x);
                    return new C4Person(path, x.getDescription(), convertLocation(x.getLocation()), tags, relationships);
                })
                .collect(toList());
        architectureDataStructure.getModel().setPeople(people);
    }

    private C4Location convertLocation(Location location) {
        return C4Location.valueOf(location.name().toUpperCase());
    }

    private void mapMetadata(Workspace workspace, ArchitectureDataStructure architectureDataStructure) {
        architectureDataStructure.setName(workspace.getName());
        architectureDataStructure.setBusinessUnit(workspace.getModel().getEnterprise().getName());
        architectureDataStructure.setDescription(workspace.getDescription());
    }

    private List<C4Tag> convertTags(String tags) {
        return Arrays.stream(tags.split(",")).map(C4Tag::new).collect(toList());
    }

    private List<C4Relationship> mapRelationships(Element fromElement, Set<Relationship> relationships) {
        return relationships
                .stream()
                .map(r -> {
                    C4Path destination = buildPath(r.getDestination());
                    C4Action action = convertAction(fromElement, destination);
                    return new C4Relationship(action, destination, r.getDescription(), r.getTechnology());
                }).collect(toList());
    }

    private C4Action convertAction(Element fromElement, C4Path destination) {
        C4Action action;
        if (fromElement instanceof Person && destination.getType().equals(C4Type.person)){
            action = C4Action.INTERACTS_WITH;
        } else if (destination.getType().equals(C4Type.person)) {
            action = C4Action.DELIVERS;
        } else {
            action = C4Action.USES;
        }
        return action;
    }
}
