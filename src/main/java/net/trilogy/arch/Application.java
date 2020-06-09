package net.trilogy.arch;

import net.trilogy.arch.adapter.git.GitInterface;
import net.trilogy.arch.adapter.google.GoogleDocsAuthorizedApiFactory;
import net.trilogy.arch.adapter.jira.JiraApiFactory;
import net.trilogy.arch.commands.*;
import net.trilogy.arch.commands.architectureUpdate.*;
import net.trilogy.arch.facade.FilesFacade;
import picocli.CommandLine;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class Application {

    private final CommandLine cli;

    public Application() throws GeneralSecurityException, IOException {
        this(new GoogleDocsAuthorizedApiFactory(), new JiraApiFactory(), new FilesFacade(), new GitInterface());
    }

    public Application(GoogleDocsAuthorizedApiFactory googleDocsApiFactory,
                       JiraApiFactory jiraApiFactory,
                       FilesFacade filesFacade,
                       GitInterface gitInterface) {
        cli = new CommandLine(new ParentCommand())
                .addSubcommand(new InitializeCommand())
                .addSubcommand(new ValidateCommand())
                .addSubcommand(new PublishCommand())
                .addSubcommand(new ImportCommand())
                .addSubcommand(new ListComponentsCommand(filesFacade))
                .addSubcommand(new DiffArchitectureCommand(filesFacade, gitInterface))
                .addSubcommand(
                        new CommandLine(new AuCommand())
                                .addSubcommand(new AuInitializeCommand(filesFacade))
                                .addSubcommand(new AuNewCommand(googleDocsApiFactory, filesFacade, gitInterface))
                                .addSubcommand(new AuValidateCommand(filesFacade, gitInterface))
                                .addSubcommand(new AuPublishStoriesCommand(jiraApiFactory, filesFacade, gitInterface))
                                .addSubcommand(new AuAnnotateCommand(filesFacade))
                );
    }

    public static void main(String[] args) throws GeneralSecurityException, IOException {
        var app = new Application();

        int exitCode = app.execute(args);

        System.exit(exitCode);
    }

    protected int execute(String[] args) {
        return cli.execute(args);
    }
}
