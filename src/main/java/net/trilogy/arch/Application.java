package net.trilogy.arch;

import net.trilogy.arch.adapter.FilesFacade;
import net.trilogy.arch.adapter.Jira.JiraApiFactory;
import net.trilogy.arch.adapter.in.google.GoogleDocsAuthorizedApiFactory;
import net.trilogy.arch.commands.*;
import net.trilogy.arch.commands.architectureUpdate.*;
import picocli.CommandLine;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class Application {

    private final CommandLine cli;

    public Application(GoogleDocsAuthorizedApiFactory googleDocsApiFactory,
                       JiraApiFactory jiraApiFactory,
                       FilesFacade filesFacade) {
        cli = new CommandLine(new ParentCommand())
                .addSubcommand(new InitializeCommand())
                .addSubcommand(new ValidateCommand())
                .addSubcommand(new PublishCommand())
                .addSubcommand(new ImportCommand())
                .addSubcommand(
                        new CommandLine(new AuCommand())
                                .addSubcommand(new AuInitializeCommand(filesFacade))
                                .addSubcommand(new AuNewCommand(googleDocsApiFactory, filesFacade))
                                .addSubcommand(new AuValidateCommand())
                                .addSubcommand(new AuPublishStoriesCommand(jiraApiFactory, filesFacade))
                                .addSubcommand(new AuAnnotateCommand(filesFacade))
                );
    }

    public static void main(String[] args) throws GeneralSecurityException, IOException {
        var filesFacade = new FilesFacade();
        var googleDocsApiFactory = new GoogleDocsAuthorizedApiFactory();
        var jiraApiFactory = new JiraApiFactory();

        var app = new Application(googleDocsApiFactory, jiraApiFactory, filesFacade);

        int exitCode = app.execute(args);
        System.exit(exitCode);
    }

    protected int execute(String[] args) {
        return cli.execute(args);
    }
}
