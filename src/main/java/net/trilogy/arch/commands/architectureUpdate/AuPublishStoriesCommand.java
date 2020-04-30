package net.trilogy.arch.commands.architectureUpdate;

import net.trilogy.arch.adapter.FilesFacade;
import net.trilogy.arch.adapter.Jira.JiraApi;
import net.trilogy.arch.adapter.Jira.JiraApiFactory;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "publish", description = "Publish stories.")
public class AuPublishStoriesCommand implements Callable<Integer> {

    private final JiraApiFactory jiraApiFactory;
    private FilesFacade filesFacade;
    @CommandLine.Option(names = {"-u", "--username"}, description = "Username")
    private File username;

    @CommandLine.Option(names = {"-p", "--password"}, arity = "0..1", interactive = true)
    private char[] password;

    @CommandLine.Parameters(index = "0", description = "Product documentation root directory")
    private File productDocumentationRoot;

    public AuPublishStoriesCommand(JiraApiFactory jiraApiFactory, FilesFacade filesFacade) {
        this.jiraApiFactory = jiraApiFactory;
        this.filesFacade = filesFacade;
    }

    public Integer call() throws IOException, InterruptedException {

        final JiraApi jiraApi = jiraApiFactory.create(filesFacade);
        jiraApi.createStory();

        return 0;
    }
}
