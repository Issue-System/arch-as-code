package net.trilogy.arch.commands.architectureUpdate;

import net.trilogy.arch.adapter.ArchitectureUpdateObjectMapper;
import net.trilogy.arch.adapter.FilesFacade;
import net.trilogy.arch.adapter.Jira.JiraApi;
import net.trilogy.arch.adapter.Jira.JiraApiFactory;
import net.trilogy.arch.adapter.Jira.JiraStory;
import net.trilogy.arch.domain.architectureUpdate.ArchitectureUpdate;
import net.trilogy.arch.domain.architectureUpdate.Jira;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@CommandLine.Command(name = "publish", description = "Publish stories.")
public class AuPublishStoriesCommand implements Callable<Integer> {

    private final JiraApiFactory jiraApiFactory;
    private final FilesFacade filesFacade;

    @CommandLine.Parameters(index = "0", description = "File name of architecture update to validate")
    private File architectureUpdateFileName;

    @CommandLine.Parameters(index = "1", description = "Product documentation root directory")
    private File productDocumentationRoot;

    @CommandLine.Option(names = {"-u", "--username"}, description = "Username")
    private File username;

    @CommandLine.Option(names = {"-p", "--password"}, arity = "0..1", interactive = true)
    private char[] password;

    @CommandLine.Spec
    private CommandLine.Model.CommandSpec spec;


    public AuPublishStoriesCommand(JiraApiFactory jiraApiFactory, FilesFacade filesFacade) {
        this.jiraApiFactory = jiraApiFactory;
        this.filesFacade = filesFacade;
    }

    public Integer call() throws IOException, InterruptedException {

        Path auPath = architectureUpdateFileName.toPath();

        ArchitectureUpdate au;
        try {
            au = new ArchitectureUpdateObjectMapper().readValue(Files.readString(auPath));
        } catch (IOException | RuntimeException e) {
            spec.commandLine().getErr().println("Invalid structure. Error thrown: \n" + e.getMessage() + "\nCause: " + e.getCause());
            return 1;
        }

        final JiraApi jiraApi = jiraApiFactory.create(filesFacade);
        jiraApi.createStories(getFeatureStories(au), getJira(au));

        return 0;
    }

    private Jira getJira(ArchitectureUpdate au) {
        return au.getCapabilityContainer().getEpic().getJira();
    }

    private List<JiraStory> getFeatureStories(ArchitectureUpdate au) {
        return au.getCapabilityContainer().getFeatureStories().stream().map(fs -> new JiraStory(au, fs)).collect(Collectors.toList());
    }
}
