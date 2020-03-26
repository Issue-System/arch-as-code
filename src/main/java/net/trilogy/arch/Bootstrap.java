package net.trilogy.arch;

import net.trilogy.arch.commands.ImportCommand;
import net.trilogy.arch.commands.InitializeCommand;
import net.trilogy.arch.commands.ParentCommand;
import net.trilogy.arch.commands.PublishCommand;
import net.trilogy.arch.commands.ValidateCommand;
import net.trilogy.arch.commands.architectureUpdate.ArchitectureUpdateCommand;
import net.trilogy.arch.commands.architectureUpdate.AuInitializeCommand;
import net.trilogy.arch.commands.architectureUpdate.AuNewCommand;
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
                .addSubcommand(
                        new CommandLine(new ArchitectureUpdateCommand())
                                .addSubcommand(new AuInitializeCommand())
                                .addSubcommand(new AuNewCommand())
                )
                .execute(args);
    }
}
