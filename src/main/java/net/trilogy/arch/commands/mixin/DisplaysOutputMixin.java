package net.trilogy.arch.commands.mixin;

import picocli.CommandLine;

public interface DisplaysOutputMixin {
    CommandLine.Model.CommandSpec getSpec();

    default void print(String message) {
        getSpec().commandLine().getOut().println(message);
    }
}
