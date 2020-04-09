package net.trilogy.arch.adapter;

import net.trilogy.arch.domain.architectureUpdate.ArchitectureUpdate;
import net.trilogy.arch.domain.architectureUpdate.Capability;
import net.trilogy.arch.domain.architectureUpdate.Epic;
import net.trilogy.arch.domain.architectureUpdate.Jira;
import net.trilogy.arch.domain.architectureUpdate.Link;
import net.trilogy.arch.domain.architectureUpdate.MilestoneDependency;
import net.trilogy.arch.domain.architectureUpdate.P1;
import net.trilogy.arch.domain.architectureUpdate.P2;
import net.trilogy.arch.domain.architectureUpdate.Person;
import net.trilogy.arch.domain.architectureUpdate.Requirement;
import net.trilogy.arch.domain.architectureUpdate.TDD;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


public class ArchitectureUpdateObjectMapperTest {

//    @Test
//    public void shouldWriteBlank() throws Exception {
//        String actual = new ArchitectureUpdateObjectMapper().writeValueAsString(ArchitectureUpdate.blank());
//
//        String expected = String.join("\n"
//                , "" // does nothing
//                , "name: \"\""
//                , "milestone: \"\""
//                , "authors:"
//                , "- name: \"\""
//                , "  email: \"\""
//                , "PCAs:"
//                , "- name: \"\""
//                , "  email: \"\""
//                , "requirements:"
//                , "  ' ': ' '"
//                , "TDDs:"
//                , "  'Component- ':"
//                , "  - ' '"
//                , "capabilities:"
//                , "  E2Es: \"\""
//                , "  ACCs: \"\""
//                , "  stories:"
//                , "  - TDDs:"
//                , "    - \"\""
//                , "    requirements:"
//                , "    - \"\""
//                , "P2:"
//                , "  link: \"\""
//                , "  jira:"
//                , "    ticket: \"\""
//                , "    link: \"\""
//                , "P1:"
//                , "  link: \"\""
//                , "  jira:"
//                , "    ticket: \"\""
//                , "    link: \"\""
//                , "  executive-summary: \"\""
//                , "useful-links:"
//                , "- description: \"\""
//                , "  link: \"\""
//                , "milestone-dependencies:"
//                , "- description: \"\""
//                , "  links:"
//                , "  - description: \"\""
//                , "    link: \"\""
//        );
//
//        assertThat(actual.trim(), equalTo(expected.trim()));
//    }

    @Test
    public void shouldWrite() throws Exception {
        ArchitectureUpdate architectureUpdate = new ArchitectureUpdate(
                "name",
                "milestone",
                List.of(new Person("author", "email")),
                List.of(new Person("PCA", "email")),
                Map.of(new Requirement.Id("ITD 1.1"), new Requirement("requirement", List.of(new TDD.Id("id")))),
                Map.of(new TDD.ComponentReference("42"), List.of(new TDD(new TDD.Id("id"), "TDD placeholder text"))),
                List.of("sample e2e text"),
                new Epic(
                        "epic title",
                        new Jira("epic-jira-ticket", "epic-jira-ticket-link"),
                        List.of(new Capability(
                                new Jira("jira-ticket", "jira-ticket-link"),
                                List.of(new TDD.Id("id")),
                                List.of(new Requirement.Id("ITD 1.1"))
                        ))
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
                , "requirements:"
                , "  ITD 1.1:"
                , "    text: requirement"
                , "    tdd-references:"
                , "    - TDD-id"
                , "TDDs:"
                , "  Component-42:"
                , "  - id: TDD-id"
                , "    text: TDD placeholder text"
                , "E2Es:"
                , "- sample e2e text"
                , "epic:"
                , "  title: epic title"
                , "  jira:"
                , "    ticket: epic-jira-ticket"
                , "    link: epic-jira-ticket-link"
                , "  capabilities:"
                , "  - jira:"
                , "      ticket: jira-ticket"
                , "      link: jira-ticket-link"
                , "    tdd-references:"
                , "    - TDD-id"
                , "    requirement-references:"
                , "    - ITD 1.1"
        );

        assertThat(actual.trim(), equalTo(expected.trim()));
    }
}
