package net.trilogy.arch.commands.architectureUpdate;

import lombok.Getter;
import net.trilogy.arch.adapter.architectureUpdateYaml.ArchitectureUpdateObjectMapper;
import net.trilogy.arch.adapter.architectureYaml.ArchitectureDataStructureObjectMapper;
import net.trilogy.arch.adapter.git.GitInterface;
import net.trilogy.arch.adapter.jira.JiraApi;
import net.trilogy.arch.adapter.jira.JiraApiFactory;
import net.trilogy.arch.adapter.jira.JiraStory.InvalidStoryException;
import net.trilogy.arch.commands.mixin.DisplaysErrorMixin;
import net.trilogy.arch.commands.mixin.DisplaysOutputMixin;
import net.trilogy.arch.commands.mixin.LoadArchitectureFromGitMixin;
import net.trilogy.arch.commands.mixin.LoadArchitectureMixin;
import net.trilogy.arch.domain.ArchitectureDataStructure;
import net.trilogy.arch.domain.architectureUpdate.ArchitectureUpdate;
import net.trilogy.arch.facade.FilesFacade;
import net.trilogy.arch.services.architectureUpdate.StoryPublishingService;
import picocli.CommandLine;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "publish", description = "Publish stories.", mixinStandardHelpOptions = true)
public class AuPublishStoriesCommand implements Callable<Integer>, LoadArchitectureMixin, LoadArchitectureFromGitMixin, DisplaysOutputMixin {

    private final JiraApiFactory jiraApiFactory;
    private final ArchitectureUpdateObjectMapper architectureUpdateObjectMapper;
    @Getter
    private final ArchitectureDataStructureObjectMapper architectureDataStructureObjectMapper;
    @Getter
    private final FilesFacade filesFacade;
    @Getter
    private final GitInterface gitInterface;

    @CommandLine.Parameters(index = "0", description = "File name of architecture update to validate")
    private File architectureUpdateFileName;

    @Getter
    @CommandLine.Parameters(index = "1", description = "Product architecture root directory")
    private File productArchitectureDirectory;

    @CommandLine.Option(names = {"-b", "--branch-of-base-architecture"}, description = "Name of git branch from which this AU was branched. Used to get names of components. Usually 'master'. Also can be a commit or tag.", required = true)
    String baseBranch;

    @CommandLine.Option(names = {"-u", "--username"}, description = "Jira username", required = true)
    private String username;

    @CommandLine.Option(names = {"-p", "--password"}, description = "Jira password", arity = "0..1", interactive = true, required = true)
    private char[] password;

    @Getter
    @CommandLine.Spec
    private CommandLine.Model.CommandSpec spec;

    public AuPublishStoriesCommand(JiraApiFactory jiraApiFactory, FilesFacade filesFacade, GitInterface gitInterface) {
        this.jiraApiFactory = jiraApiFactory;
        this.architectureUpdateObjectMapper = new ArchitectureUpdateObjectMapper();
        this.filesFacade = filesFacade;
        this.gitInterface = gitInterface;
        architectureDataStructureObjectMapper = new ArchitectureDataStructureObjectMapper();
    }

    public Integer call() {
        logArgs();
        final Path auPath = architectureUpdateFileName.toPath();

        var au = loadAu(auPath);
        if (au.isEmpty()) return 1;

        final var beforeAuArchitecture = loadArchitectureFromGitOrPrintError(baseBranch, "Unable to load product architecture in branch: " + baseBranch);
        if (beforeAuArchitecture.isEmpty()) return 1;

        final var afterAuArchitecture = loadArchitectureOrPrintError("Unable to load architecture.");
        if (afterAuArchitecture.isEmpty()) return 1;

        var jiraApi = getJiraApi();
        if (jiraApi.isEmpty()) return 1;

        final StoryPublishingService jiraService = new StoryPublishingService(
                spec.commandLine().getOut(),
                spec.commandLine().getErr(),
                jiraApi.get());

        var updatedAu = createStories(au.get(), beforeAuArchitecture.get(), afterAuArchitecture.get(), jiraService);
        if (updatedAu.isEmpty()) return 1;

        try {
            filesFacade.writeString(auPath, architectureUpdateObjectMapper.writeValueAsString(updatedAu.get()));
        } catch (Exception e) {
            printError("Unable to write update to AU.", e);
            return 1;
        }

        return 0;
    }

    private Optional<ArchitectureUpdate> createStories(ArchitectureUpdate au, ArchitectureDataStructure beforeAuArchitecture, ArchitectureDataStructure afterAuArchitecture, StoryPublishingService jiraService) {
        try {
            return Optional.of(jiraService.createStories(au, beforeAuArchitecture, afterAuArchitecture, username, password));
        } catch (JiraApi.JiraApiException e) {
            printError("Jira API failed", e);
        } catch (StoryPublishingService.NoStoriesToCreateException ignored) {
            printError("ERROR: No stories to create.");
        } catch (InvalidStoryException e) {
            printError("ERROR: Some stories are invalid. Please run 'au validate' command.");
        }

        return Optional.empty();
    }

    private Optional<JiraApi> getJiraApi() {
        try {
            return Optional.of(jiraApiFactory.create(filesFacade, productArchitectureDirectory.toPath()));
        } catch (Exception e) {
            printError("Unable to load configuration.", e);
            return Optional.empty();
        }
    }

    private Optional<ArchitectureUpdate> loadAu(Path auPath) {
        try {
            return Optional.of(architectureUpdateObjectMapper.readValue(filesFacade.readString(auPath)));
        } catch (Exception e) {
            printError("Unable to load architecture update.", e);
            return Optional.empty();
        }
    }
}

