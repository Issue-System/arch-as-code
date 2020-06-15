package net.trilogy.arch;

import net.trilogy.arch.adapter.git.GitInterface;
import net.trilogy.arch.adapter.google.GoogleDocsAuthorizedApiFactory;
import net.trilogy.arch.adapter.graphviz.GraphvizInterface;
import net.trilogy.arch.adapter.jira.JiraApiFactory;
import net.trilogy.arch.commands.*;
import net.trilogy.arch.commands.architectureUpdate.*;
import net.trilogy.arch.facade.FilesFacade;
import picocli.CommandLine;

import java.io.IOException;
import java.security.GeneralSecurityException;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor
public class Application {

    @Builder.Default
    private final GoogleDocsAuthorizedApiFactory googleDocsAuthorizedApiFactory = new GoogleDocsAuthorizedApiFactory();
    @Builder.Default
    private final JiraApiFactory jiraApiFactory = new JiraApiFactory();
    @Builder.Default
    private final FilesFacade filesFacade = new FilesFacade();
    @Builder.Default
    private final GitInterface gitInterface = new GitInterface();
    @Builder.Default
    private final GraphvizInterface graphvizInterface = new GraphvizInterface();

    private CommandLine getCli() {
        return new CommandLine(new ParentCommand())
                .addSubcommand(new InitializeCommand())
                .addSubcommand(new ValidateCommand())
                .addSubcommand(new PublishCommand())
                .addSubcommand(new ImportCommand())
                .addSubcommand(new ListComponentsCommand(filesFacade))
                .addSubcommand(new DiffCommand(filesFacade, gitInterface, graphvizInterface))
                .addSubcommand(
                        new CommandLine(new AuCommand())
                                .addSubcommand(new AuInitializeCommand(filesFacade))
                                .addSubcommand(new AuNewCommand(googleDocsAuthorizedApiFactory, filesFacade, gitInterface))
                                .addSubcommand(new AuValidateCommand(filesFacade, gitInterface))
                                .addSubcommand(new AuPublishStoriesCommand(jiraApiFactory, filesFacade, gitInterface))
                                .addSubcommand(new AuAnnotateCommand(filesFacade))
                );
    }

    public static void main(String[] args) throws GeneralSecurityException, IOException {
        final var app = Application.builder().build();

        int exitCode = app.execute(args);

        System.exit(exitCode);
    }

    protected int execute(String[] args) {
        return getCli().execute(args);
    }
}
