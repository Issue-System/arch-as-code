package net.trilogy.arch.adapter;

import net.trilogy.arch.domain.architectureUpdate.ArchitectureUpdate;
import net.trilogy.arch.domain.architectureUpdate.Capabilities;
import net.trilogy.arch.domain.architectureUpdate.Jira;
import net.trilogy.arch.domain.architectureUpdate.Link;
import net.trilogy.arch.domain.architectureUpdate.MilestoneDependency;
import net.trilogy.arch.domain.architectureUpdate.P1;
import net.trilogy.arch.domain.architectureUpdate.P2;
import net.trilogy.arch.domain.architectureUpdate.Person;
import net.trilogy.arch.domain.architectureUpdate.Requirement;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


public class ArchitectureUpdateObjectMapperTest {

    @Test
    public void shouldWriteBlank() throws Exception {
        String actual = new ArchitectureUpdateObjectMapper().writeValueAsString(ArchitectureUpdate.blank());

        String expected = String.join("\n"
                , "" // does nothing
                , "name: \"\""
                , "milestone: \"\""
                , "authors:"
                , "- name: \"\""
                , "  email: \"\""
                , "PCAs:"
                , "- name: \"\""
                , "  email: \"\""
                , "requirements:"
                , "  ' ': ' '"
                , "capabilities:"
                , "  E2Es: \"\""
                , "  ACCs: \"\""
                , "  stories:"
                , "  - TDDs:"
                , "    - \"\""
                , "    requirements:"
                , "    - \"\""
                , "P2:"
                , "  link: \"\""
                , "  jira:"
                , "    ticket: \"\""
                , "    link: \"\""
                , "P1:"
                , "  link: \"\""
                , "  jira:"
                , "    ticket: \"\""
                , "    link: \"\""
                , "  executive-summary: \"\""
                , "useful-links:"
                , "- description: \"\""
                , "  link: \"\""
                , "milestone-dependencies:"
                , "- description: \"\""
                , "  links:"
                , "  - description: \"\""
                , "    link: \"\""
        );

        assertThat(actual.trim(), equalTo(expected.trim()));
    }

    @Test
    public void shouldWrite() throws Exception {
        ArchitectureUpdate architectureUpdate = new ArchitectureUpdate(
                "name",
                "milestone",
                List.of(new Person("author", "email")),
                List.of(new Person("PCA", "email")),
                Map.of(new Requirement.Id("ITD 1.1"), new Requirement("requirement")),
                new Capabilities(
                        "e2e placeholder text",
                        "acc placeholder text",
                        List.of(new Capabilities.Story(List.of("tdd 1"), List.of("ITD 1.1")))
                ),
                new P2("link", new Jira("ticket", "link")),
                new P1("link", new Jira("ticket", "link"), "summary"),
                List.of(new Link("description", "link")),
                List.of(new MilestoneDependency("description", List.of(new Link("description", "link")))));

        String actual = new ArchitectureUpdateObjectMapper().writeValueAsString(architectureUpdate);

        String expected = String.join("\n"
                , ""
                , "name: name"
                , "milestone: milestone"
                , "authors:"
                , "- name: author"
                , "  email: email"
                , "PCAs:"
                , "- name: PCA"
                , "  email: email"
                , "requirements:"
                , "  ITD 1.1: requirement"
                , "capabilities:"
                , "  E2Es: e2e placeholder text"
                , "  ACCs: acc placeholder text"
                , "  stories:"
                , "  - TDDs:"
                , "    - tdd 1"
                , "    requirements:"
                , "    - ITD 1.1"
                , "P2:"
                , "  link: link"
                , "  jira:"
                , "    ticket: ticket"
                , "    link: link"
                , "P1:"
                , "  link: link"
                , "  jira:"
                , "    ticket: ticket"
                , "    link: link"
                , "  executive-summary: summary"
                , "useful-links:"
                , "- description: description"
                , "  link: link"
                , "milestone-dependencies:"
                , "- description: description"
                , "  links:"
                , "  - description: description"
                , "    link: link"
        );

        assertThat(actual.trim(), equalTo(expected.trim()));
    }
}
