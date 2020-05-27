package net.trilogy.arch.commands;

import picocli.CommandLine;

public interface DisplaysErrorMixin {

    CommandLine.Model.CommandSpec getSpec();

    default void printError(String errorMessage) {
        getSpec().commandLine().getErr().println(errorMessage);
    }

    default void printError(String errorMessage, Exception exception) {
        getSpec().commandLine().getErr().println(errorMessage + "\nError thrown: " + exception + "\nCause: " + exception.getCause());
    }
}
