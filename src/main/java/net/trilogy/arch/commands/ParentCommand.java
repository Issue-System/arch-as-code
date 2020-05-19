package net.trilogy.arch.commands;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.Callable;

import picocli.CommandLine.Command;
import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

@Command(
        name = "arch-as-code",
        description = "Architecture as code",
        mixinStandardHelpOptions = true,
        versionProvider = ParentCommand.VersionProvider.class
)
public class ParentCommand implements Callable<Integer> {
    @Spec
    private CommandSpec spec;

    @Override
    public Integer call() {
        spec.commandLine().getOut().println(spec.commandLine().getUsageMessage());
        return 0;
    }

    static class VersionProvider implements CommandLine.IVersionProvider {
        @Override
        public String[] getVersion() throws IOException {
            URL url = getClass().getResource("/version.txt");
            checkNotNull(url, "Failed to retrieve version information.");
            Properties properties = new Properties();
            properties.load(url.openStream());
            return new String[]{"arch-as-code version " + properties.getProperty("Version")};
        }
    }
}
