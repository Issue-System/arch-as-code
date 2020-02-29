package net.nahknarmi.arch.commands;

import com.google.common.io.Files;
import com.structurizr.Workspace;
import com.structurizr.io.plantuml.PlantUMLWriter;
import com.structurizr.view.View;
import io.vavr.Tuple2;
import net.nahknarmi.arch.publish.ArchitectureDataStructurePublisher;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "preview", description = "Generate preview of architecture model.")
public class PreviewCommand implements Callable<Integer> {
    private static final Log logger = LogFactory.getLog(PreviewCommand.class);

    @CommandLine.Parameters(index = "0", paramLabel = "PRODUCT_DOCUMENTATION_PATH", description = "Product documentation root where data-structure.yml is located.")
    File productDocumentationRoot;
    private final String manifestFileName;
    private static final String PREVIEW_DIRECTORY = ".preview";

    // Only for testing purposes
    public PreviewCommand(File productDocumentationRoot, String manifestFileName) {
        this.productDocumentationRoot = productDocumentationRoot;
        this.manifestFileName = manifestFileName;
    }

    // For production
    public PreviewCommand() {
        this.manifestFileName = "data-structure.yml";
    }


    @Override
    public Integer call() throws Exception {
        Workspace workspace = ArchitectureDataStructurePublisher.create(productDocumentationRoot, manifestFileName).getWorkspace(productDocumentationRoot, manifestFileName);

        workspace.getViews().getViews().forEach(x -> {
            File file = new File(PREVIEW_DIRECTORY + File.separator + x.getClass().getSimpleName());
            try {
                java.nio.file.Files.deleteIfExists(Paths.get(file.getAbsolutePath()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            file.mkdirs();
        });

        workspace.getViews()
                .getViews()
                .stream()
                .map(view -> new Tuple2<>(new PlantUMLWriter().toString(view), view))
                .forEach(tuple2 -> {
                    try {
                        View view = tuple2._2();
                        File pumlFile = new File(PREVIEW_DIRECTORY + File.separator + view.getClass().getSimpleName() + File.separator + tuple2._2().getKey() + ".puml");
                        logger.info("Writing view '" + pumlFile.getAbsolutePath() + "'");
                        Files.write(tuple2._1().getBytes(), pumlFile);
                    } catch (IOException e) {
                        throw new IllegalStateException("Failed to write view.", e);
                    }
                });

        return 0;
    }
}
