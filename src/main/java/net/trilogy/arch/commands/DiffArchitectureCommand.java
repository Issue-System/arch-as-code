package net.trilogy.arch.commands;

import lombok.Getter;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.trilogy.arch.adapter.architectureYaml.ArchitectureDataStructureObjectMapper;
import net.trilogy.arch.adapter.git.GitInterface;
import net.trilogy.arch.domain.Diff;
import net.trilogy.arch.facade.FilesFacade;
import net.trilogy.arch.services.ArchitectureDiffCalculator;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "diff", mixinStandardHelpOptions = true, description = "Display the diff between product architecture in current branch and specified branch.")
public class DiffArchitectureCommand implements Callable<Integer>, LoadArchitectureMixin, LoadArchitectureFromGitBranchMixin {
    @Getter private final GitInterface gitInterface;
    @Getter private final FilesFacade filesFacade;
    @Getter private final ArchitectureDataStructureObjectMapper architectureDataStructureObjectMapper;

    @Getter
    @CommandLine.Spec
    private CommandLine.Model.CommandSpec spec;

    @Getter
    @CommandLine.Parameters(index = "0", description = "Product architecture root directory")
    private File productArchitectureDirectory;

    @CommandLine.Option(names = {"-b", "--branch-of-diff-architecture"}, description = "Name of git branch to compare against current architecture. Usually 'master'.", required = true)
    String baseBranch;

    public DiffArchitectureCommand(FilesFacade filesFacade, GitInterface gitInterface) {
        this.filesFacade = filesFacade;
        this.gitInterface = gitInterface;
        this.architectureDataStructureObjectMapper = new ArchitectureDataStructureObjectMapper();
    }

    @Override
    public Integer call() throws IOException {

        String source = "@startuml\ntestdot\n@enduml";

        SourceStringReader reader = new SourceStringReader(source);
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        // Write the first image to "os"
        DiagramDescription desc = reader.outputImage(os, new FileFormatOption(FileFormat.SVG));
        os.close();

        // The XML is stored into svg
        final String svg = new String(os.toByteArray(), Charset.forName("UTF-8"));

        getFilesFacade().writeString(Path.of("/tmp/aac/test.svg"), svg);

        return 0;

        // final var currentArch = loadArchitectureOrPrintError("Unable to load architecture file");
        // if (currentArch.isEmpty()) return 1;

        // final var beforeArch = loadArchitectureOfBranchOrPrintError(baseBranch, "Unable to load '" + baseBranch + "' branch architecture");
        // if (beforeArch.isEmpty()) return 1;

        // final Set<Diff> diff = ArchitectureDiffCalculator.diff(beforeArch.get(), currentArch.get());
        // spec.commandLine().getOut().println(diff);

        // return 0;
    }
}
