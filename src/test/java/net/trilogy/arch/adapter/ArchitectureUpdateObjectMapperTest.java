package net.trilogy.arch.adapter;

import net.trilogy.arch.domain.ArchitectureUpdate;
import net.trilogy.arch.domain.ArchitectureUpdate.MilestoneDependency;
import net.trilogy.arch.domain.ArchitectureUpdate.P1;
import net.trilogy.arch.domain.ArchitectureUpdate.P2;
import net.trilogy.arch.domain.Jira;
import net.trilogy.arch.domain.Link;
import net.trilogy.arch.domain.Person;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


public class ArchitectureUpdateObjectMapperTest {

    @Test
    public void shouldWrite() throws IOException {
        ArchitectureUpdate architectureUpdate = new ArchitectureUpdate(
                "name",
                "milestone",
                List.of(new Person("author", "email")),
                List.of(new Person("PCA", "email")),
                new P2("link", new Jira("ticket", "link")),
                new P1("link", new Jira("ticket", "link"), "summary"),
                List.of(new Link("description", "link")),
                List.of(new MilestoneDependency("description", List.of(new Link("description", "link")))));
        File tempFile = getTempFile();
        new ArchitectureUpdateObjectMapper().writeValue(tempFile, architectureUpdate);

        assertThat(
                Files.readString(tempFile.toPath()),
                equalTo(
                        "name: \"name\"\n" +
                                "milestone: \"milestone\"\n" +
                                "authors:\n" +
                                "- name: \"author\"\n" +
                                "  email: \"email\"\n" +
                                "PCAs:\n" +
                                "- name: \"PCA\"\n" +
                                "  email: \"email\"\n" +
                                "P2:\n" +
                                "  link: \"link\"\n" +
                                "  jira:\n" +
                                "    ticket: \"ticket\"\n" +
                                "    link: \"link\"\n" +
                                "P1:\n" +
                                "  link: \"link\"\n" +
                                "  jira:\n" +
                                "    ticket: \"ticket\"\n" +
                                "    link: \"link\"\n" +
                                "  summary: \"summary\"\n" +
                                "useful-links:\n" +
                                "- description: \"description\"\n" +
                                "  link: \"link\"\n" +
                                "milestone-dependencies:\n" +
                                "- description: \"description\"\n" +
                                "  links:\n" +
                                "  - description: \"description\"\n" +
                                "    link: \"link\"\n"
                )
        );
    }

    private File getTempFile() throws IOException {
        return File.createTempFile("abc", "def");
    }
}
