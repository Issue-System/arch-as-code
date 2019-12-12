package net.nahknarmi.arch.preview;

import com.structurizr.Workspace;
import com.structurizr.io.plantuml.PlantUMLWriter;
import net.nahknarmi.arch.domain.ArchitectureDataStructure;
import net.nahknarmi.arch.transformation.enhancer.WorkspaceEnhancer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ArchitectureDataStructurePreviewer implements WorkspaceEnhancer {

    @Override
    public void enhance(Workspace workspace, ArchitectureDataStructure dataStructure) {
        PlantUMLWriter writer = new PlantUMLWriter();
        new File(".preview").mkdir();
        try (FileWriter fileWriter = new FileWriter(".preview/diagrams.puml")) {

            writer.write(workspace, fileWriter);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write preview diagram.", e);
        }
    }
}
