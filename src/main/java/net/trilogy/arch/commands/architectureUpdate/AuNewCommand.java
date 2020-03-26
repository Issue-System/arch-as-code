package net.trilogy.arch.commands.architectureUpdate;

import net.trilogy.arch.adapter.ArchitectureUpdateObjectMapper;
import net.trilogy.arch.domain.ArchitectureUpdate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "new", description = "Initialize a new architecture update.")
public class AuNewCommand implements Callable<Integer> {
    private static final Log logger = LogFactory.getLog(ArchitectureUpdateCommand.class);
    private static final ArchitectureUpdateObjectMapper objectMapper = new ArchitectureUpdateObjectMapper();

    @CommandLine.Parameters(index = "0", description = "Name for new architecture update")
    private String name;

    @CommandLine.Parameters(index = "1", description = "Product documentation root directory")
    private File productDocumentationRoot;

    @Override
    public Integer call() throws IOException {
        File auFolder = Helpers.getAuFolder(productDocumentationRoot);

        if (!auFolder.isDirectory()) {
            logger.error(String.format("Root path - %s - seems incorrect. Run init first.", auFolder.getAbsolutePath()));
            return 1;
        }

        String auFileName = name + ".yml";

        File auFile = auFolder.toPath().resolve(auFileName).toFile();
        if (auFile.isFile()) {
            logger.error(String.format("AU %s already exists. Try a different name.", auFileName));
            return 1;
        }

        ArchitectureUpdate au = ArchitectureUpdate.blank();
        Files.writeString(auFile.toPath(), objectMapper.writeValueAsString(au));

        logger.info(String.format("AU created - %s", auFile.toPath()));
        return 0;
    }
}
