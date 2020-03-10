package net.trilogy.arch.e2e;

import net.trilogy.arch.commands.InitializeCommand;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
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
    public void initialize() throws Exception {
        InitializeCommand command = new InitializeCommand("key", "secret", "1234", tempProductDirectory.toFile());
        assertThat(command.call(), equalTo(0));

        //check that credentials.json file created
        File credentialsFile = tempProductDirectory.resolve(".arch-as-code/structurizr/credentials.json").toFile();
        assertTrue(credentialsFile.exists());
        assertTrue(credentialsFile.isFile());

        //check that data-structure.yml file created
        assertTrue(tempProductDirectory.resolve("data-structure.yml").toFile().exists());
    }
}
