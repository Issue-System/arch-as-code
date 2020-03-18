package net.trilogy.arch.e2e;

import net.trilogy.arch.commands.InitializeCommand;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertTrue;


public class InitializeCommandE2ETest {
    private Path tempProductDirectory;

    @Before
    public void setUp() throws Exception {
        tempProductDirectory = Files.createTempDirectory("arch-as-code");
    }

    @After
    public void tearDown() throws Exception {
        Files.walk(tempProductDirectory).map(Path::toFile).forEach(File::delete);
    }

    @Test
    public void shouldInitializeCredentials() throws Exception {
        InitializeCommand command = new InitializeCommand("key", "secret", "1234", tempProductDirectory.toFile());
        assertThat(command.call(), equalTo(0));

        File file = tempProductDirectory.resolve(".arch-as-code/structurizr/credentials.json").toFile();
        assertTrue(file.exists());
        assertTrue(file.isFile());
        assertThat(Files.readAllLines(file.toPath()),
                contains("{\"workspace_id\":\"key\",\"api_key\":\"secret\",\"api_secret\":\"1234\"}"));
    }

    @Test
    public void shouldInitializeDataStructureYamlFile() throws Exception {
        InitializeCommand command = new InitializeCommand("key", "secret", "1234", tempProductDirectory.toFile());
        assertThat(command.call(), equalTo(0));

        File file = tempProductDirectory.resolve("data-structure.yml").toFile();
        assertTrue(file.exists());
        assertThat(Files.readAllLines(file.toPath()),
                contains(
                        "name: \"Hello World!!!\"",
                        "businessUnit: \"DevFactory\"",
                        "description: \"Architecture as code\"",
                        "decisions: []",
                        "model:",
                        "  people: []",
                        "  systems: []",
                        "  containers: []",
                        "  components: []",
                        "  deploymentNodes: []",
                        "views:",
                        "  systemViews: []",
                        "  containerViews: []",
                        "  componentViews: []",
                        "  deploymentViews: []"
                )
        );
    }

}
