package net.trilogy.arch.e2e;

import net.trilogy.arch.TestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertTrue;

public class ImportCommandE2ETest {
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
    public void shouldImportStructurizrJsonFileToYaml() throws Exception {
        File workspacePath = new File(getClass().getResource("/structurizr/Think3-Sococo.c4model.json").getPath());
        final String pathToSococo = workspacePath.getAbsolutePath();
        assertThat(TestHelper.execute("import", pathToSococo, tempProductDirectory.toAbsolutePath().toString()), equalTo(0));

        File file = tempProductDirectory.resolve("data-structure.yml").toFile();
        assertTrue(file.exists());
        assertTrue(Files.readString(file.toPath()).contains("Sococo Import"));
    }

    @Test
    public void shouldImportStructurizrJsonFileToSql() throws GeneralSecurityException, IOException {
        File workspacePath = new File(getClass().getResource("/structurizr/Think3-Sococo.c4model.json").getPath());
        final String pathToSococo = workspacePath.getAbsolutePath();
        assertThat(TestHelper.execute("import", pathToSococo, tempProductDirectory.toAbsolutePath().toString()), equalTo(0));

        File file = tempProductDirectory.resolve("architecture.csv").toFile();
        assertTrue(file.exists());

        System.out.println("\n\n*************\n\n");

        String result = Files.readString(file.toPath());
        System.out.println(result);

        System.out.println("\n\n*************\n\n");


//        assertTrue(Files.readString(file.toPath()).contains("Sococo Import"));
    }
}
