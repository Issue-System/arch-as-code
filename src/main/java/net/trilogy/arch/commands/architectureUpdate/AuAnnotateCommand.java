package net.trilogy.arch.commands.architectureUpdate;

import net.trilogy.arch.adapter.FilesFacade;
import net.trilogy.arch.adapter.in.ArchitectureDataStructureReader;
import net.trilogy.arch.domain.ArchitectureDataStructure;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Command(name = "annotate", description = "Annotates the architecture update with comments detailing the full paths of all components referenced by ID. Makes the AU easier to read.", mixinStandardHelpOptions = true)
public class AuAnnotateCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "File name of architecture update to annotate")
    private File architectureUpdateFilePath;

    @Parameters(index = "1", description = "Product documentation root directory")
    private File productDocumentationRoot;

    @Spec
    private CommandSpec spec;

    private final FilesFacade filesFacade;

    public AuAnnotateCommand(FilesFacade filesFacade) {
        this.filesFacade = filesFacade;
    }

    @Override
    public Integer call() {
        var regexToGetComponentReferences = Pattern.compile("(\\n\\s+['\"]?Component-)(\\d+)(['\"]?:)(\\n)");

        String au = null;
        try {
            au = filesFacade.readString(architectureUpdateFilePath.toPath());
        } catch (Exception e) {
            spec.commandLine().getErr().println("Unable to load Architecture Update." +
                    "\nError: " + e + "\nCause: " + e.getCause());
            return 2;
        }

        final Matcher matcher = regexToGetComponentReferences.matcher(au);

        final ArchitectureDataStructure architecture;
        try {
            architecture = new ArchitectureDataStructureReader(filesFacade)
                    .load(productDocumentationRoot.toPath().resolve("data-structure.yml").toFile());
        } catch (Exception e) {
            spec.commandLine().getErr().println("Unable to load Architecture data-structure.yml." +
                    "\nError: " + e + "\nCause: " + e.getCause());
            return 2;
        }

        while (matcher.find()) {
            au = matcher.replaceAll((res) -> res.group(1) + res.group(2) + res.group(3) + getComponentPathComment(res.group(2), architecture) + res.group(4));
        }

        try {
            filesFacade.writeString(architectureUpdateFilePath.toPath(), au);
        } catch (Exception e) {
            spec.commandLine().getErr().println("Unable to write annotations to Architecture Update." +
                    "\nError: " + e + "\nCause: " + e.getCause());
            return 2;
        }

        return 0;
    }

    private String getComponentPathComment(String id, ArchitectureDataStructure architecture) {
        return "  # " + architecture.getModel().findEntityById(id).getPath().getPath();
    }
}
