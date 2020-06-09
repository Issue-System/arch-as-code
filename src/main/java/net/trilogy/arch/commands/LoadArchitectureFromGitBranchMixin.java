package net.trilogy.arch.commands;

import net.trilogy.arch.adapter.git.GitInterface;
import net.trilogy.arch.domain.ArchitectureDataStructure;

import java.io.File;
import java.util.Optional;

public interface LoadArchitectureFromGitBranchMixin extends DisplaysErrorMixin {

    File getProductArchitectureDirectory();

    GitInterface getGitInterface();

    default Optional<ArchitectureDataStructure> loadArchitectureOfBranchOrPrintError(String branch, String errorMessageIfFailed) {
        final var productArchitecturePath = getProductArchitectureDirectory()
                .toPath()
                .resolve("product-architecture.yml");
        try {
            return Optional.of(
                    getGitInterface().load(branch, productArchitecturePath)
            );
        } catch (final Exception e) {
            printError(errorMessageIfFailed, e);
            return Optional.empty();
        }
    }
}
