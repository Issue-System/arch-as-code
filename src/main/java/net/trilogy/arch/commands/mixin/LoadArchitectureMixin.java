package net.trilogy.arch.commands.mixin;

import net.trilogy.arch.adapter.architectureYaml.ArchitectureDataStructureObjectMapper;
import net.trilogy.arch.commands.mixin.DisplaysErrorMixin;
import net.trilogy.arch.domain.ArchitectureDataStructure;
import net.trilogy.arch.facade.FilesFacade;

import java.io.File;
import java.util.Optional;

public interface LoadArchitectureMixin extends DisplaysErrorMixin {

    File getProductArchitectureDirectory();

    ArchitectureDataStructureObjectMapper getArchitectureDataStructureObjectMapper();

    FilesFacade getFilesFacade();

    default Optional<ArchitectureDataStructure> loadArchitectureOrPrintError(String errorMessageIfFailed) {
        final var productArchitecturePath = getProductArchitectureDirectory()
                .toPath().resolve("product-architecture.yml");

        try {
            return Optional.of(
                    getArchitectureDataStructureObjectMapper().readValue(
                            getFilesFacade().readString(productArchitecturePath)
                    )
            );
        } catch (final Exception e) {
            printError(errorMessageIfFailed, e);
            return Optional.empty();
        }
    }
}
