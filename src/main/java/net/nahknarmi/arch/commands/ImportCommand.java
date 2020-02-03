package net.nahknarmi.arch.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.structurizr.Workspace;
import com.structurizr.model.Location;
import com.structurizr.util.WorkspaceUtils;
import net.nahknarmi.arch.domain.ArchitectureDataStructure;
import net.nahknarmi.arch.domain.c4.*;
import picocli.CommandLine;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

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
        mapMetadata(workspace, architectureDataStructure);
        mapPeople(workspace, architectureDataStructure);
        mapSoftwareSystems(workspace, architectureDataStructure);
        mapContainers(workspace, architectureDataStructure);

        mapComponents(workspace, architectureDataStructure);

        File tempFile = File.createTempFile("arch-as-code", "yml");
        new ObjectMapper(new YAMLFactory()).writeValue(tempFile, architectureDataStructure);


        return new PublishCommand(tempFile.getParentFile(), tempFile.getName()).call();
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
                                    C4Path c4Path = new C4Path("c4://" + s.getName() + "/" + c.getName() + "/" + co.getName());
                                    List<C4Tag> tags = emptyList();
                                    List<C4Relationship> relationships = emptyList();
                                    return new C4Component(c4Path, co.getTechnology(), co.getDescription(), tags, relationships);
                                })

                        )

                ).collect(Collectors.toList());
        architectureDataStructure.getModel().setComponents(c4Components);
    }

    private void mapContainers(Workspace workspace, ArchitectureDataStructure architectureDataStructure) {
        //containers
        List<C4Container> containers = workspace.getModel()
                .getSoftwareSystems()
                .stream()
                .flatMap(s -> s.getContainers().stream().map(c -> {
                    List<C4Relationship> relationships = emptyList();
                    List<C4Tag> tags = emptyList();

                    return new C4Container(new C4Path("c4://" + s.getName() + "/" + c.getName()), c.getTechnology(), c.getDescription(), tags, relationships);
                }))
                .collect(Collectors.toList());
        architectureDataStructure.getModel().setContainers(containers);
    }

    private void mapSoftwareSystems(Workspace workspace, ArchitectureDataStructure architectureDataStructure) {
        List<C4SoftwareSystem> softwareSystems = workspace.getModel()
                .getSoftwareSystems()
                .stream()
                .map(x -> {
                    List<C4Relationship> relationships = emptyList();
                    List<C4Tag> tags = emptyList();
                    return new C4SoftwareSystem(new C4Path("c4://" + x.getName()), x.getDescription(), convertLocation(x.getLocation()), tags, relationships);
                })
                .collect(Collectors.toList());
        architectureDataStructure.getModel().setSystems(softwareSystems);
    }

    private void mapPeople(Workspace workspace, ArchitectureDataStructure architectureDataStructure) {
        //map people
        List<C4Person> people = workspace.getModel()
                .getPeople()
                .stream()
                .map(x -> {
                    List<C4Relationship> relationships = emptyList();
                    List<C4Tag> tags = emptyList();
                    return new C4Person(new C4Path("@" + x.getName()), x.getDescription(), convertLocation(x.getLocation()), tags, relationships);
                })
                .collect(Collectors.toList());
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
}
