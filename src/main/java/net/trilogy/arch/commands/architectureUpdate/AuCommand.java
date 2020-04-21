package net.trilogy.arch.commands.architectureUpdate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

import java.util.concurrent.Callable;

@Command(
        name = "architecture-update",
        aliases = "au",
        description = "Parent for Architecture Update commands"
)
public class AuCommand implements Callable<Integer> {
    public static final String ARCHITECTURE_UPDATES_ROOT_FOLDER = "architecture-updates";

    private static final Log logger = LogFactory.getLog(AuCommand.class);

    @Spec
    private CommandSpec spec;

    @Override
    public Integer call() {
        // TODO FUTURE: Don't use logger to print output
        logger.info(spec.commandLine().getUsageMessage());
        return 0;
    }
}
