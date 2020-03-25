package net.trilogy.arch.adapter;

import net.trilogy.arch.domain.ArchitectureUpdate;
import net.trilogy.arch.domain.Person;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;


public class ArchitectureUpdateObjectMapperTest {

    @Test
    public void shouldWrite() throws IOException {
        ArchitectureUpdate architectureUpdate = new ArchitectureUpdate(
                "name",
                "milestone",
                List.of(new Person("author")),
                List.of(new Person("PCA"))
        );
        File tempFile = getTempFile();
        new ArchitectureUpdateObjectMapper().writeValue(tempFile, architectureUpdate);

        assertThat(
                Files.readAllLines(tempFile.toPath()),
                contains(
                        "name: \"name\"",
                        "milestone: \"milestone\"",
                        "authors:",
                        "- name: \"author\"",
                        "PCAs:",
                        "- name: \"PCA\""
                )
        );
    }

    private File getTempFile() throws IOException {
        return File.createTempFile("abc", "def");
    }
}
