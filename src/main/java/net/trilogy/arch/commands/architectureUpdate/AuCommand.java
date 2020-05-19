package net.trilogy.arch.commands.architectureUpdate;

import java.util.concurrent.Callable;

import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

@Command(
        name = "architecture-update",
        aliases = "au",
        description = "Namespace for Architecture Update commands.",
        mixinStandardHelpOptions=true
)
public class AuCommand implements Callable<Integer> {
    public static final String ARCHITECTURE_UPDATES_ROOT_FOLDER = "architecture-updates";

    @Spec
    private CommandSpec spec;

    @Override
    public Integer call() {
        spec.commandLine().getOut().println(spec.commandLine().getUsageMessage());
        return 0;
    }
}
