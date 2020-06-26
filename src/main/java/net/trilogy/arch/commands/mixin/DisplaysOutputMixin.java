package net.trilogy.arch.commands.mixin;

import org.apache.logging.log4j.LogManager;
import picocli.CommandLine;

public interface DisplaysOutputMixin {
    CommandLine.Model.CommandSpec getSpec();

    default void print(String message) {
        getSpec().commandLine().getOut().println(message);

        LogManager.getLogger(getClass()).info(message);
    }
}
