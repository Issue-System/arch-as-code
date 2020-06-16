package net.trilogy.arch.commands;

import java.io.File;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.Callable;

import lombok.Getter;
import net.trilogy.arch.adapter.architectureYaml.ArchitectureDataStructureObjectMapper;
import net.trilogy.arch.adapter.git.GitInterface;
import net.trilogy.arch.adapter.graphviz.GraphvizInterface;
import net.trilogy.arch.domain.diff.Diff;
import net.trilogy.arch.domain.diff.DiffSet;
import net.trilogy.arch.facade.FilesFacade;
import net.trilogy.arch.services.ArchitectureDiffCalculator;
import net.trilogy.arch.services.DiffToDotCalculator;
import picocli.CommandLine;

@CommandLine.Command(name = "diff", mixinStandardHelpOptions = true, description = "Display the diff between product architecture in current branch and specified branch.")
public class DiffCommand implements Callable<Integer>, LoadArchitectureMixin, LoadArchitectureFromGitBranchMixin {
    @Getter private final GitInterface gitInterface;
    @Getter private final FilesFacade filesFacade;
    private final GraphvizInterface graphvizInterface;
    @Getter private final ArchitectureDataStructureObjectMapper architectureDataStructureObjectMapper;

    @Getter
    @CommandLine.Spec
    private CommandLine.Model.CommandSpec spec;

    @Getter
    @CommandLine.Parameters(index = "0", description = "Product architecture root directory")
    private File productArchitectureDirectory;

    @CommandLine.Option(names = {"-b", "--branch-of-diff-architecture"}, description = "Name of git branch to compare against current architecture. Usually 'master'.", required = true)
    private String baseBranch;

    @CommandLine.Option(names = {"-o", "--output-directory"}, description = "New directory in which svg files will be created.", required = true)
    private File outputDirectory;

    public DiffCommand(FilesFacade filesFacade, GitInterface gitInterface, GraphvizInterface graphvizInterface) {
        this.filesFacade = filesFacade;
        this.gitInterface = gitInterface;
        this.graphvizInterface = graphvizInterface;
        this.architectureDataStructureObjectMapper = new ArchitectureDataStructureObjectMapper();
    }

    @Override
    public Integer call() {
        final var currentArch = loadArchitectureOrPrintError("Unable to load architecture file");
        if (currentArch.isEmpty()) return 1;

        final var beforeArch = loadArchitectureOfBranchOrPrintError(baseBranch, "Unable to load '" + baseBranch + "' branch architecture");
        if (beforeArch.isEmpty()) return 1;

        final Path outputDir;
        try {
            outputDir = filesFacade.createDirectory(outputDirectory.toPath());
        } catch (Exception e) {
            printError("Unable to create output directory", e);
            return 1;
        }

        final DiffSet diffSet = ArchitectureDiffCalculator.diff(beforeArch.get(), currentArch.get());
        Set<Diff> systemLevelDiffs = diffSet.getSystemLevelDiffs();

        var success = render(systemLevelDiffs, null, outputDir.resolve("system-context-diagram.svg"));
        for(var system : systemLevelDiffs) {
            if(!success) return 1;
            String systemId = system.getElement().getId();
            Set<Diff> containerLevelDiffs = diffSet.getContainerLevelDiffs(systemId);
            if(containerLevelDiffs.size() == 0) continue;
            success = render(containerLevelDiffs, system, outputDir.resolve("assets/" + systemId + ".svg"));
        }
        if(!success) return 1;

        spec.commandLine().getOut().println("SVG files created in " + outputDir.toAbsolutePath().toString());

        return 0;
    }

    private boolean render(Set<Diff> diffs, Diff parentEntityDiff, Path outputFile) {
        final String dotGraph = DiffToDotCalculator.toDot("diff", diffs, parentEntityDiff);

        try {
            graphvizInterface.render(dotGraph, outputFile);
        } catch (Exception e) {
            printError("Unable to render SVG", e);
            return false;
        }

        return true;
    }
}
