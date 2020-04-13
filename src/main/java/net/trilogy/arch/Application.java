package net.trilogy.arch;

import net.trilogy.arch.adapter.FilesFacade;
import net.trilogy.arch.adapter.in.google.GoogleDocsAuthorizedApiFactory;
import net.trilogy.arch.commands.ImportCommand;
import net.trilogy.arch.commands.InitializeCommand;
import net.trilogy.arch.commands.ParentCommand;
import net.trilogy.arch.commands.PublishCommand;
import net.trilogy.arch.commands.ValidateCommand;
import net.trilogy.arch.commands.architectureUpdate.ArchitectureUpdateCommand;
import net.trilogy.arch.commands.architectureUpdate.AuInitializeCommand;
import net.trilogy.arch.commands.architectureUpdate.AuNewCommand;
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
                        new CommandLine(new ArchitectureUpdateCommand())
                                .addSubcommand(new AuInitializeCommand(filesFacade))
                                .addSubcommand(new AuNewCommand(googleDocsApiFactory))
                );
    }

    public static void main(String[] args) throws GeneralSecurityException, IOException {
        var googleDocsApiFactory = new GoogleDocsAuthorizedApiFactory();
        var filesAdapter = new FilesFacade();

        var app = new Application(googleDocsApiFactory, filesAdapter);

        int exitCode = app.execute(args);
        System.exit(exitCode);
    }

    protected int execute(String[] args) {
        return cli.execute(args);
    }
}
