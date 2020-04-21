package net.trilogy.arch;

import net.trilogy.arch.adapter.FilesFacade;
import net.trilogy.arch.adapter.in.google.GoogleDocsAuthorizedApiFactory;
import net.trilogy.arch.commands.*;
import net.trilogy.arch.commands.architectureUpdate.AuCommand;
import net.trilogy.arch.commands.architectureUpdate.AuInitializeCommand;
import net.trilogy.arch.commands.architectureUpdate.AuNewCommand;
import net.trilogy.arch.commands.architectureUpdate.AuValidateCommand;
import picocli.CommandLine;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class Application {

    private final CommandLine cli;

    public Application(GoogleDocsAuthorizedApiFactory googleDocsApiFactory, FilesFacade filesFacade) {
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
                );
    }

    public static void main(String[] args) throws GeneralSecurityException, IOException {
        var googleDocsApiFactory = new GoogleDocsAuthorizedApiFactory();
        var filesFacade = new FilesFacade();

        var app = new Application(googleDocsApiFactory, filesFacade);

        int exitCode = app.execute(args);
        System.exit(exitCode);
    }

    protected int execute(String[] args) {
        return cli.execute(args);
    }
}
