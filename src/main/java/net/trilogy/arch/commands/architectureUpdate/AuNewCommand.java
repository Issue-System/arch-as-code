package net.trilogy.arch.commands.architectureUpdate;

import lombok.Getter;
import net.trilogy.arch.adapter.ArchitectureUpdateObjectMapper;
import net.trilogy.arch.adapter.FilesFacade;
import net.trilogy.arch.adapter.GitFacade;
import net.trilogy.arch.adapter.in.google.GoogleDocsApiInterface;
import net.trilogy.arch.adapter.in.google.GoogleDocsAuthorizedApiFactory;
import net.trilogy.arch.adapter.in.google.GoogleDocumentReader;
import net.trilogy.arch.commands.DisplaysErrorMixin;
import net.trilogy.arch.domain.architectureUpdate.ArchitectureUpdate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NoWorkTreeException;

import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "new", mixinStandardHelpOptions = true, description = "Create a new architecture update.")
public class AuNewCommand implements Callable<Integer>, DisplaysErrorMixin {
    private static final Log logger = LogFactory.getLog(AuCommand.class);
    private static final ArchitectureUpdateObjectMapper objectMapper = new ArchitectureUpdateObjectMapper();
    private final GoogleDocsAuthorizedApiFactory googleDocsApiFactory;
    private final FilesFacade filesFacade;
    private final GitFacade gitFacade;

    @CommandLine.Parameters(index = "0", description = "Name for new architecture update")
    private String name;

    @CommandLine.Parameters(index = "1", description = "Product architecture root directory")
    private File productArchitectureDirectory;

    @CommandLine.Option(names = {"-p", "--p1-url"}, description = "Url to P1 Google Document, used to import decisions and other data", required = false)
    private String p1GoogleDocUrl;

    @Getter
    @CommandLine.Spec
    private CommandLine.Model.CommandSpec spec;

    public AuNewCommand(GoogleDocsAuthorizedApiFactory googleDocsApiFactory, FilesFacade filesFacade, GitFacade gitFacade) {
        this.googleDocsApiFactory = googleDocsApiFactory;
        this.filesFacade = filesFacade;
        this.gitFacade = gitFacade;
    }

    @Override
    public Integer call() throws NoWorkTreeException, GitAPIException, IOException {
        String branchName = gitFacade.open(productArchitectureDirectory).getRepository().getBranch();
        if(!name.equals(branchName)){
            printError(
                "ERROR: AU must be created in git branch of same name."+
                "\nCurrent git branch: '" + branchName + "'" +
                "\nAu name: '" + name + "'"
            );
            return 1;
        }

        File auFolder = productArchitectureDirectory.toPath().resolve(AuCommand.ARCHITECTURE_UPDATES_ROOT_FOLDER).toFile();

        if (!auFolder.isDirectory()) {
            try {
                filesFacade.createDirectory(auFolder.toPath());
            } catch (Exception e) {
                printError( "Unable to create architecture-updates directory.", e);
                return 1;
            }
        }

        String auFileName = name + ".yml";

        File auFile = auFolder.toPath().resolve(auFileName).toFile();
        if (auFile.isFile()) {
            logger.error(String.format("AU %s already exists. Try a different name.", auFileName));
            return 1;
        }

        ArchitectureUpdate au = ArchitectureUpdate.builderPreFilledWithBlanks().name(name).build();

        try {
            if(p1GoogleDocUrl != null) {
                GoogleDocsApiInterface authorizedDocsApi = googleDocsApiFactory.getAuthorizedDocsApi(productArchitectureDirectory);
                au = new GoogleDocumentReader(authorizedDocsApi).load(p1GoogleDocUrl);
            }
        } catch (Exception e) {
            String configPath = productArchitectureDirectory.toPath().resolve(".arch-as-code").toAbsolutePath().toString();
            printError( "ERROR: Unable to initialize Google Docs API. Does configuration " + configPath + " exist?", e);
            return 1;
        }

        try {
            filesFacade.writeString(auFile.toPath(), objectMapper.writeValueAsString(au));
        } catch (Exception e) {
            printError("Unable to write AU file.", e);
            return 1;
        }

        spec.commandLine().getOut().println(String.format("AU created - %s", auFile.toPath()));
        return 0;
    }
}
