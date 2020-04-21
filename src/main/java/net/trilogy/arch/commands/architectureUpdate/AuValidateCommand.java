package net.trilogy.arch.commands.architectureUpdate;

import picocli.CommandLine;

import java.io.File;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "validate", description = "Validate Architecture Update")
public class AuValidateCommand implements Callable<Integer> {
    @CommandLine.Parameters(index = "0", description = "Product documentation root directory")
    private File productDocumentationRoot;

    @Override
    public Integer call() {
        return 0;
    }
}
