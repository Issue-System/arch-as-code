package net.trilogy.arch.commands.architectureUpdate;

import net.trilogy.arch.adapter.ArchitectureUpdateObjectMapper;
import net.trilogy.arch.adapter.FilesFacade;
import net.trilogy.arch.adapter.Jira.JiraApi;
import net.trilogy.arch.adapter.Jira.JiraApiFactory;
import net.trilogy.arch.adapter.out.ArchitectureDataStructureWriter;
import net.trilogy.arch.domain.architectureUpdate.ArchitectureUpdate;
import net.trilogy.arch.services.architectureUpdate.StoryPublishingService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "publish", description = "Publish stories.", mixinStandardHelpOptions = true)
public class AuPublishStoriesCommand implements Callable<Integer> {

    private static final Log logger = LogFactory.getLog(AuPublishStoriesCommand.class);

    private final JiraApiFactory jiraApiFactory;
    private final FilesFacade filesFacade;
    private final ArchitectureUpdateObjectMapper architectureUpdateObjectMapper;

    @CommandLine.Parameters(index = "0", description = "File name of architecture update to validate")
    private File architectureUpdateFileName;

    @CommandLine.Parameters(index = "1", description = "Product documentation root directory")
    private File productDocumentationRoot;

    @CommandLine.Option(names = {"-u", "--username"}, description = "Username", required = true)
    private String username;

    @CommandLine.Option(names = {"-p", "--password"}, arity = "0..1", interactive = true, required = true)
    private char[] password;

    @CommandLine.Spec
    private CommandLine.Model.CommandSpec spec;

    public AuPublishStoriesCommand(JiraApiFactory jiraApiFactory, FilesFacade filesFacade) {
        this.jiraApiFactory = jiraApiFactory;
        this.filesFacade = filesFacade;
        this.architectureUpdateObjectMapper = new ArchitectureUpdateObjectMapper();
    }

    public Integer call() throws IOException {
        Path auPath = architectureUpdateFileName.toPath();

        ArchitectureUpdate au;
        try {
            au = architectureUpdateObjectMapper.readValue(Files.readString(auPath));
        } catch (IOException | RuntimeException e) {
            spec.commandLine().getErr().println("Invalid structure. Error thrown: \n" + e.getMessage() + "\nCause: " + e.getCause());
            return 1;
        }

        final JiraApi jiraApi = jiraApiFactory.create(filesFacade, productDocumentationRoot.toPath());
        var stdOut = spec.commandLine().getOut();
        final StoryPublishingService jiraService = new StoryPublishingService(stdOut, jiraApi);

        final ArchitectureUpdate updatedAu;
        try {
            updatedAu = jiraService.createStories(au, username, password);
        } catch (JiraApi.JiraApiException e) {
            spec.commandLine().getErr().println("ERROR: " + e.getMessage());
            return 1;
        }

       filesFacade.writeString(auPath, architectureUpdateObjectMapper.writeValueAsString(updatedAu));

        return 0;
    }
}
