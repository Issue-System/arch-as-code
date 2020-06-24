package net.trilogy.arch.commands.architectureUpdate;

import lombok.Getter;
import net.trilogy.arch.adapter.architectureYaml.ArchitectureDataStructureObjectMapper;
import net.trilogy.arch.commands.mixin.DisplaysErrorMixin;
import net.trilogy.arch.commands.mixin.DisplaysOutputMixin;
import net.trilogy.arch.commands.mixin.LoadArchitectureMixin;
import net.trilogy.arch.domain.ArchitectureDataStructure;
import net.trilogy.arch.facade.FilesFacade;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Command(name = "annotate", description = "Annotates the architecture update with comments detailing the full paths of all components referenced by ID. Makes the AU easier to read.", mixinStandardHelpOptions = true)
public class AuAnnotateCommand implements Callable<Integer>, LoadArchitectureMixin, DisplaysOutputMixin, DisplaysErrorMixin {

    @Parameters(index = "0", description = "File name of architecture update to annotate")
    private File architectureUpdateFilePath;

    @Getter
    @Parameters(index = "1", description = "Product architecture root directory")
    private File productArchitectureDirectory;

    @Getter
    @Spec
    private CommandSpec spec;

    @Getter
    private final FilesFacade filesFacade;

    @Getter
    private final ArchitectureDataStructureObjectMapper architectureDataStructureObjectMapper;

    public AuAnnotateCommand(FilesFacade filesFacade) {
        this.filesFacade = filesFacade;
        architectureDataStructureObjectMapper = new ArchitectureDataStructureObjectMapper();
    }

    @Override
    public Integer call() {
        var regexToGetComponentReferences = Pattern.compile("(\\n\\s+['\"]?Component-)(\\d+)(['\"]?:)[^\\n]*(\\n)");

        String au = null;
        try {
            au = filesFacade.readString(architectureUpdateFilePath.toPath());
        } catch (Exception e) {
            printError(e, "Unable to load Architecture Update.");
            return 2;
        }

        final Matcher matcher = regexToGetComponentReferences.matcher(au);

        final var architecture = loadArchitectureOrPrintError("Unable to load Architecture product-architecture.yml.");
        if(architecture.isEmpty()) return 2;

        while (matcher.find()) {
            au = matcher.replaceAll((res) -> 
                    res.group(1) + 
                    res.group(2) + 
                    res.group(3) + 
                    getComponentPathComment(res.group(2), architecture.get()) +
                    res.group(4)
            );
        }

        try {
            filesFacade.writeString(architectureUpdateFilePath.toPath(), au);
        } catch (Exception e) {
            printError(e, "Unable to write annotations to Architecture Update.");
            return 2;
        }

        print("AU has been annotated with component paths.");
        return 0;
    }

    private void printError(Exception e, String s) {
        printError(s +
                "\nError: " + e + "\nCause: " + e.getCause());
    }

    private String getComponentPathComment(String id, ArchitectureDataStructure architecture) {
        try {
            return "  # " + architecture.getModel().findEntityById(id).orElseThrow(() -> new IllegalStateException("Could not find entity with id: " + id)).getPath().getPath();
        } catch (Exception ignored) {
            return "";
        }
    }
}
