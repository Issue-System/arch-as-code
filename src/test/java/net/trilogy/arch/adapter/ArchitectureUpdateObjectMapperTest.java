package net.trilogy.arch.adapter;

import net.trilogy.arch.domain.ArchitectureUpdate;
import net.trilogy.arch.domain.ArchitectureUpdate.MilestoneDependency;
import net.trilogy.arch.domain.ArchitectureUpdate.P1;
import net.trilogy.arch.domain.ArchitectureUpdate.P2;
import net.trilogy.arch.domain.Jira;
import net.trilogy.arch.domain.Link;
import net.trilogy.arch.domain.Person;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


public class ArchitectureUpdateObjectMapperTest {

    @Test
    public void shouldWrite() throws Exception {
        ArchitectureUpdate architectureUpdate = new ArchitectureUpdate(
                "name",
                "milestone",
                List.of(new Person("author", "email")),
                List.of(new Person("PCA", "email")),
                List.of(new ArchitectureUpdate.Requirement("req1", ArchitectureUpdate.RequirementType.ITD, "requirement")),
                new P2("link", new Jira("ticket", "link")),
                new P1("link", new Jira("ticket", "link"), "summary"),
                List.of(new Link("description", "link")),
                List.of(new MilestoneDependency("description", List.of(new Link("description", "link")))));

        String actual = new ArchitectureUpdateObjectMapper().writeValueAsString(architectureUpdate);

        String expected = String.join("\n"
                , ""
                , "name: \"name\""
                , "milestone: \"milestone\""
                , "authors:"
                , "- name: \"author\""
                , "  email: \"email\""
                , "PCAs:"
                , "- name: \"PCA\""
                , "  email: \"email\""
                , "requirements:"
                , "- type: \"ITD\""
                , "  id: \"req1\""
                , "  requirement: \"requirement\""
                , "P2:"
                , "  link: \"link\""
                , "  jira:"
                , "    ticket: \"ticket\""
                , "    link: \"link\""
                , "P1:"
                , "  link: \"link\""
                , "  jira:"
                , "    ticket: \"ticket\""
                , "    link: \"link\""
                , "  executive-summary: \"summary\""
                , "useful-links:"
                , "- description: \"description\""
                , "  link: \"link\""
                , "milestone-dependencies:"
                , "- description: \"description\""
                , "  links:"
                , "  - description: \"description\""
                , "    link: \"link\""
        );

        assertThat(actual.trim(), equalTo(expected.trim()));
    }
}
