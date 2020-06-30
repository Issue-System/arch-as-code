package net.trilogy.arch.commands.architectureUpdate;

import java.util.concurrent.Callable;

import lombok.Getter;
import net.trilogy.arch.commands.mixin.DisplaysOutputMixin;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

@Command(
        name = "architecture-update",
        aliases = "au",
        description = "Namespace for Architecture Update commands.",
        mixinStandardHelpOptions=true
)
public class AuCommand implements Callable<Integer>, DisplaysOutputMixin {
    public static final String ARCHITECTURE_UPDATES_ROOT_FOLDER = "architecture-updates";

    @Getter
    @Spec
    private CommandSpec spec;

    @Override
    public Integer call() {
        logArgs();
        print(spec.commandLine().getUsageMessage());
        return 0;
    }
}
