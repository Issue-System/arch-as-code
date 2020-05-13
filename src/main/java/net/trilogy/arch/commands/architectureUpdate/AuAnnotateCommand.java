package net.trilogy.arch.commands.architectureUpdate;

import java.io.File;
import java.util.concurrent.Callable;

import net.trilogy.arch.adapter.FilesFacade;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

@Command(
    name = "annotate",
    description = "Annotates the architecture update with comments detailing the full paths of all components referenced by ID. Makes the AU easier to read.",
    mixinStandardHelpOptions = true
)
public class AuAnnotateCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "File name of architecture update to annotate")
    private File architectureUpdateFilePath;

    @Parameters(index = "1", description = "Product documentation root directory")
    private File productDocumentationRoot;

    @Spec
    private CommandSpec spec;

    public AuAnnotateCommand(FilesFacade filesFacade) {
    }

    @Override
    public Integer call() {
        return 0;
    }
}
