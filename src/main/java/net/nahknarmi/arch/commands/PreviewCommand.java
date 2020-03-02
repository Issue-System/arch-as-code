package net.nahknarmi.arch.commands;

import com.google.common.io.Files;
import com.structurizr.Workspace;
import com.structurizr.io.plantuml.PlantUMLWriter;
import com.structurizr.view.View;
import io.vavr.Tuple2;
import net.nahknarmi.arch.publish.ArchitectureDataStructurePublisher;
import net.sourceforge.plantuml.SourceStringReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

import static java.io.File.separator;

@CommandLine.Command(name = "preview", description = "Generate preview of architecture model.")
public class PreviewCommand implements Callable<Integer> {
    private static final Log logger = LogFactory.getLog(PreviewCommand.class);
    private final String EXPORT_IMAGE_EXTENSION = "png";

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

        generateViews(workspace);

        //create markdown file
        String viewsSection = workspace.getViews().getViews()
                .stream().map(x -> "## " + x.getKey() + "\n\n![](" + x.getClass().getSimpleName() + separator + x.getKey() + "." + EXPORT_IMAGE_EXTENSION + ")\n\n")
                .reduce("\n", String::concat);

        Files.write(("# C4 Model Views " + viewsSection).getBytes(), new File(".preview/index.md"));

        return 0;
    }

    private void generateViews(Workspace workspace) {
        createDirs(workspace);

        workspace.getViews()
                .getViews()
                .parallelStream()
                .map(view -> new Tuple2<>(new PlantUMLWriter().toString(view), view))
                .forEach(tuple2 -> {
                    try {
                        View view = tuple2._2();
                        File pumlFile = viewFilePath(view, "puml");
                        logger.info("Writing view '" + pumlFile.getAbsolutePath() + "'");

                        new SourceStringReader(tuple2._1()).generateImage(viewFilePath(view, EXPORT_IMAGE_EXTENSION));
                        Files.write(tuple2._1().getBytes(), pumlFile);
                    } catch (IOException e) {
                        throw new IllegalStateException("Failed to write view.", e);
                    }
                });
    }

    private void createDirs(Workspace workspace) {
        workspace.getViews().getViews().forEach(x -> {
            File file = new File(PREVIEW_DIRECTORY + separator + x.getClass().getSimpleName());
            try {
                java.nio.file.Files.deleteIfExists(Paths.get(file.getAbsolutePath()));
            } catch (IOException e) {
                throw new IllegalStateException("Failed to delete directory.", e);
            }

            file.mkdirs();
        });
    }

    private File viewFilePath(View view, final String extension) {
        return new File(PREVIEW_DIRECTORY + separator + view.getClass().getSimpleName() + separator + view.getKey() + "." + extension);
    }
}
