package net.trilogy.arch;

import net.trilogy.arch.commands.*;
import picocli.CommandLine;

public class Bootstrap {

    public static void main(String[] args) {
        int exitCode = new Bootstrap().execute(args);
        System.exit(exitCode);
    }

    protected int execute(String[] args) {
        return new CommandLine(new ParentCommand())
                .addSubcommand(new InitializeCommand())
                .addSubcommand(new ValidateCommand())
                .addSubcommand(new PublishCommand())
                .addSubcommand(new ImportCommand())
                .addSubcommand(new ArchitectureUpdateCommand())
                .execute(args);
    }
}
