package net.trilogy.arch.commands;

import lombok.Getter;
import net.trilogy.arch.adapter.architectureYaml.ArchitectureDataStructureObjectMapper;
import net.trilogy.arch.adapter.git.GitInterface;
import net.trilogy.arch.domain.ArchitectureDataStructure;
import net.trilogy.arch.domain.Diff;
import net.trilogy.arch.facade.FilesFacade;
import net.trilogy.arch.services.ArchitectureDiffCalculator;
import picocli.CommandLine;

import java.io.File;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "diff", mixinStandardHelpOptions = true, description = "Display the diff between product architecture in current branch and specified branch.")
public class DiffArchitectureCommand implements Callable<Integer>, DisplaysErrorMixin {
    private final FilesFacade filesFacade;
    private final GitInterface gitInterface;
    private final ArchitectureDataStructureObjectMapper architectureDataStructureObjectMapper;


    @CommandLine.Parameters(index = "0", description = "Product architecture root directory")
    private File productArchitectureDirectory;

    @CommandLine.Option(names = {"-b", "--branch-of-diff-architecture"}, description = "Name of git branch to compare against current architecture. Usually 'master'.", required = true)
    String baseBranch;

    @Getter
    @CommandLine.Spec
    private CommandLine.Model.CommandSpec spec;

    public DiffArchitectureCommand(FilesFacade filesFacade, GitInterface gitInterface) {
        this.filesFacade = filesFacade;
        this.gitInterface = gitInterface;
        this.architectureDataStructureObjectMapper = new ArchitectureDataStructureObjectMapper();
    }

    @Override
    public Integer call() {
        final var currentArch = loadArchitectureOfCurrentBranch();
        if (currentArch.isEmpty()) return 1;

        final var beforeArch = loadArchitectureOfBranch(baseBranch);
        if (beforeArch.isEmpty()) return 1;

        final Set<Diff> diff = ArchitectureDiffCalculator.diff(beforeArch.get(), currentArch.get());
        spec.commandLine().getOut().println(diff);

        return 0;
    }


    private Optional<ArchitectureDataStructure> loadArchitectureOfCurrentBranch() {
        final var productArchitecturePath = productArchitectureDirectory
                .toPath()
                .resolve("product-architecture.yml");

        try {
            return Optional.of(
                    architectureDataStructureObjectMapper.readValue(
                            filesFacade.readString(productArchitecturePath)
                    )
            );
        } catch (final Exception e) {
            printError("Unable to load architecture file", e);
            return Optional.empty();
        }
    }


    private Optional<ArchitectureDataStructure> loadArchitectureOfBranch(String branch) {
        final var productArchitecturePath = productArchitectureDirectory
                .toPath()
                .resolve("product-architecture.yml");
        try {
            return Optional.of(
                    gitInterface.load(branch, productArchitecturePath)
            );
        } catch (final Exception e) {
            printError("Unable to load '" + branch + "' branch architecture", e);
            return Optional.empty();
        }
    }
}
