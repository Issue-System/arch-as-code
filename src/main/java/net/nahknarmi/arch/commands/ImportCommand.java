package net.nahknarmi.arch.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.structurizr.Workspace;
import com.structurizr.util.WorkspaceUtils;
import net.nahknarmi.arch.adapter.WorkspaceConverter;
import net.nahknarmi.arch.adapter.out.*;
import net.nahknarmi.arch.domain.ArchitectureDataStructure;
import net.nahknarmi.arch.domain.c4.C4Component;
import net.nahknarmi.arch.domain.c4.C4Container;
import net.nahknarmi.arch.domain.c4.C4Person;
import net.nahknarmi.arch.domain.c4.C4SoftwareSystem;
import net.nahknarmi.arch.domain.c4.view.C4ComponentView;
import net.nahknarmi.arch.domain.c4.view.C4ContainerView;
import net.nahknarmi.arch.domain.c4.view.C4SystemView;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import picocli.CommandLine;

import java.io.File;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "import", description = "Imports existing struturizr workspace")
public class ImportCommand implements Callable<Integer> {
    private static final Log logger = LogFactory.getLog(ImportCommand.class);

    @CommandLine.Parameters(index = "0", paramLabel = "EXPORTED_WORKSPACE", description = "Exported structurizr workspace location.", defaultValue = "./")
    private File exportedWorkspacePath;

    // Only for testing purposes
    public ImportCommand(File exportedWorkspacePath) {
        this.exportedWorkspacePath = exportedWorkspacePath;
    }

    public ImportCommand() {
    }

    @Override
    public Integer call() throws Exception {
        Workspace workspace = WorkspaceUtils.loadWorkspaceFromJson(exportedWorkspacePath);
        ArchitectureDataStructure dataStructure = new WorkspaceConverter().convert(workspace);

        File tempFile = File.createTempFile("arch-as-code", ".yml");
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

        SimpleModule module = new SimpleModule();
        module.addSerializer(new C4PersonSerializer(C4Person.class));
        module.addSerializer(new C4SoftwareSystemSerializer(C4SoftwareSystem.class));
        module.addSerializer(new C4ContainerSerializer(C4Container.class));
        module.addSerializer(new C4ComponentSerializer(C4Component.class));
        module.addSerializer(new C4ContainerViewSerializer(C4ContainerView.class));
        module.addSerializer(new C4ComponentViewSerializer(C4ComponentView.class));
        module.addSerializer(new C4SystemViewSerializer(C4SystemView.class));
        objectMapper.registerModule(module);

        objectMapper.writeValue(tempFile, dataStructure);



        logger.info(String.format("Architecture data structure written to - %s", tempFile.getAbsolutePath()));

        return new PublishCommand(tempFile.getParentFile(), tempFile.getName()).call();
    }
}
