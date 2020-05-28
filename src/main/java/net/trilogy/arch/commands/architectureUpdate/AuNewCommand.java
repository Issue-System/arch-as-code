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

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
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
    public Integer call() {
        if(!checkBranchNameEquals(name)) return 1;

        var auFile = getNewAuFilePath();
        if(auFile.isEmpty()) return 1;

        var au = loadAu();
        if(au.isEmpty()) return 1;

        if(!writeAu(auFile.get(), au.get())) return 1;

        spec.commandLine().getOut().println(String.format("AU created - %s", auFile.get().toPath()));
        return 0;
    }

    private Optional<ArchitectureUpdate> loadAu() {
        if(p1GoogleDocUrl != null) {
            return loadFromP1();
        }else{
            return Optional.of(ArchitectureUpdate.builderPreFilledWithBlanks().name(name).build());
        }
    }

    private boolean writeAu(File auFile, ArchitectureUpdate au){
        try {
            filesFacade.writeString(auFile.toPath(), objectMapper.writeValueAsString(au));
            return true;
        } catch (Exception e) {
            printError("Unable to write AU file.", e);
            return false;
        }
    }

    private Optional<ArchitectureUpdate> loadFromP1(){
        try {
            GoogleDocsApiInterface authorizedDocsApi = googleDocsApiFactory.getAuthorizedDocsApi(productArchitectureDirectory);
            return Optional.of(new GoogleDocumentReader(authorizedDocsApi).load(p1GoogleDocUrl));
        } catch (Exception e) {
            String configPath = productArchitectureDirectory.toPath().resolve(".arch-as-code").toAbsolutePath().toString();
            printError( "ERROR: Unable to initialize Google Docs API. Does configuration " + configPath + " exist?", e);
            return Optional.empty();
        }
    }

    private Optional<File> getNewAuFilePath() {
        File auFolder = productArchitectureDirectory.toPath().resolve(AuCommand.ARCHITECTURE_UPDATES_ROOT_FOLDER).toFile();

        if (!auFolder.isDirectory()) {
            try {
                filesFacade.createDirectory(auFolder.toPath());
            } catch (Exception e) {
                printError( "Unable to create architecture-updates directory.", e);
                return Optional.empty();
            }
        }

        String auFileName = name + ".yml";
        File auFile = auFolder.toPath().resolve(auFileName).toFile();
        if (auFile.isFile()) {
            logger.error(String.format("AU %s already exists. Try a different name.", auFileName));
            return Optional.empty();
        }

        return Optional.of(auFile);
    }

    private boolean checkBranchNameEquals(String str) {

        try {
            String branch = gitFacade.wrap(getRepository(productArchitectureDirectory))
                .getRepository()
                .getBranch();
            if(branch.equals(str)) return true; 
            printError(
                "ERROR: AU must be created in git branch of same name."+
                "\nCurrent git branch: '" + branch + "'" +
                "\nAu name: '" + str + "'"
            );
            return false;
        } catch (Exception e) {
            printError("ERROR: Unable to check git branch", e);
            return false;
        }
    }

    public static Repository getRepository(File rootDir) throws IOException {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        return builder
                .readEnvironment()
                .findGitDir(rootDir)
                .build();
    }
}
