package net.trilogy.arch;

import net.trilogy.arch.facade.FilesFacade;
import net.trilogy.arch.facade.GitFacade;
import net.trilogy.arch.adapter.jira.JiraApiFactory;
import net.trilogy.arch.adapter.google.GoogleDocsAuthorizedApiFactory;
import net.trilogy.arch.commands.*;
import net.trilogy.arch.commands.architectureUpdate.*;
import picocli.CommandLine;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class Application {

    private final CommandLine cli;

    public Application(GoogleDocsAuthorizedApiFactory googleDocsApiFactory,
                       JiraApiFactory jiraApiFactory,
                       FilesFacade filesFacade, 
                       GitFacade gitFacade) {
        cli = new CommandLine(new ParentCommand())
                .addSubcommand(new InitializeCommand())
                .addSubcommand(new ValidateCommand())
                .addSubcommand(new PublishCommand())
                .addSubcommand(new ImportCommand())
                .addSubcommand(new ListComponentsCommand(filesFacade))
                .addSubcommand(
                        new CommandLine(new AuCommand())
                                .addSubcommand(new AuInitializeCommand(filesFacade))
                                .addSubcommand(new AuNewCommand(googleDocsApiFactory, filesFacade, gitFacade))
                                .addSubcommand(new AuValidateCommand())
                                .addSubcommand(new AuPublishStoriesCommand(jiraApiFactory, filesFacade))
                                .addSubcommand(new AuAnnotateCommand(filesFacade))
                );
    }

    public static void main(String[] args) throws GeneralSecurityException, IOException {
        var filesFacade = new FilesFacade();
        var gitFacade = new GitFacade();
        var googleDocsApiFactory = new GoogleDocsAuthorizedApiFactory();
        var jiraApiFactory = new JiraApiFactory();

        var app = new Application(googleDocsApiFactory, jiraApiFactory, filesFacade, gitFacade);

        int exitCode = app.execute(args);
        System.exit(exitCode);
    }

    protected int execute(String[] args) {
        return cli.execute(args);
    }
}
