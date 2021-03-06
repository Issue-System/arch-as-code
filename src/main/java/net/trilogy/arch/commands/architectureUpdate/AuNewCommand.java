package net.trilogy.arch.commands.architectureUpdate;

import lombok.Getter;
import net.trilogy.arch.adapter.architectureUpdateYaml.ArchitectureUpdateObjectMapper;
import net.trilogy.arch.adapter.git.GitInterface;
import net.trilogy.arch.adapter.google.GoogleDocsApiInterface;
import net.trilogy.arch.adapter.google.GoogleDocsAuthorizedApiFactory;
import net.trilogy.arch.adapter.google.GoogleDocumentReader;
import net.trilogy.arch.commands.mixin.DisplaysErrorMixin;
import net.trilogy.arch.commands.mixin.DisplaysOutputMixin;
import net.trilogy.arch.domain.architectureUpdate.ArchitectureUpdate;
import net.trilogy.arch.facade.FilesFacade;
import picocli.CommandLine;

import java.io.File;
import java.util.Optional;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "new", mixinStandardHelpOptions = true, description = "Create a new architecture update.")
public class AuNewCommand implements Callable<Integer>, DisplaysErrorMixin, DisplaysOutputMixin {
    private static final ArchitectureUpdateObjectMapper objectMapper = new ArchitectureUpdateObjectMapper();
    private final GoogleDocsAuthorizedApiFactory googleDocsApiFactory;
    private final FilesFacade filesFacade;
    private final GitInterface gitInterface;

    @CommandLine.Parameters(index = "0", description = "Name for new architecture update")
    private String name;

    @CommandLine.Parameters(index = "1", description = "Product architecture root directory")
    private File productArchitectureDirectory;

    @CommandLine.Option(names = {"-p", "--p1-url"}, description = "Url to P1 Google Document, used to import decisions and other data", required = false)
    private String p1GoogleDocUrl;

    @Getter
    @CommandLine.Spec
    private CommandLine.Model.CommandSpec spec;

    public AuNewCommand(GoogleDocsAuthorizedApiFactory googleDocsApiFactory, FilesFacade filesFacade, GitInterface gitInterface) {
        this.googleDocsApiFactory = googleDocsApiFactory;
        this.filesFacade = filesFacade;
        this.gitInterface = gitInterface;
    }

    @Override
    public Integer call() {
        logArgs();
        if (!checkBranchNameEquals(name)) return 1;

        var auFile = getNewAuFilePath();
        if (auFile.isEmpty()) return 1;

        var au = loadAu();
        if (au.isEmpty()) return 1;

        if (!writeAu(auFile.get(), au.get())) return 1;

        print(String.format("AU created - %s", auFile.get().toPath()));
        return 0;
    }

    private Optional<ArchitectureUpdate> loadAu() {
        if (p1GoogleDocUrl != null) {
            return loadFromP1();
        } else {
            return Optional.of(ArchitectureUpdate.builderPreFilledWithBlanks().name(name).build());
        }
    }

    private boolean writeAu(File auFile, ArchitectureUpdate au) {
        try {
            filesFacade.writeString(auFile.toPath(), objectMapper.writeValueAsString(au));
            return true;
        } catch (Exception e) {
            printError("Unable to write AU file.", e);
            return false;
        }
    }

    private Optional<ArchitectureUpdate> loadFromP1() {
        try {
            GoogleDocsApiInterface authorizedDocsApi = googleDocsApiFactory.getAuthorizedDocsApi(productArchitectureDirectory);
            return Optional.of(new GoogleDocumentReader(authorizedDocsApi).load(p1GoogleDocUrl));
        } catch (Exception e) {
            String configPath = productArchitectureDirectory.toPath().resolve(".arch-as-code").toAbsolutePath().toString();
            printError("ERROR: Unable to initialize Google Docs API. Does configuration " + configPath + " exist?", e);
            return Optional.empty();
        }
    }

    private Optional<File> getNewAuFilePath() {
        File auFolder = productArchitectureDirectory.toPath().resolve(AuCommand.ARCHITECTURE_UPDATES_ROOT_FOLDER).toFile();

        if (!auFolder.isDirectory()) {
            try {
                filesFacade.createDirectory(auFolder.toPath());
            } catch (Exception e) {
                printError("Unable to create architecture-updates directory.", e);
                return Optional.empty();
            }
        }

        String auFileName = name + ".yml";
        File auFile = auFolder.toPath().resolve(auFileName).toFile();
        if (auFile.isFile()) {
            printError(String.format("AU %s already exists. Try a different name.", auFileName));
            return Optional.empty();
        }

        return Optional.of(auFile);
    }

    private boolean checkBranchNameEquals(String str) {
        try {
            String branch = gitInterface.getBranch(productArchitectureDirectory);
            if (branch.equals(str)) return true;
            printError(
                    "ERROR: AU must be created in git branch of same name." +
                            "\nCurrent git branch: '" + branch + "'" +
                            "\nAu name: '" + str + "'"
            );
            return false;
        } catch (Exception e) {
            printError("ERROR: Unable to check git branch", e);
            return false;
        }
    }
}
