package net.trilogy.arch.adapter;

import net.trilogy.arch.domain.architectureUpdate.ArchitectureUpdate;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ArchitectureUpdateObjectMapperTest {

    @Test
    public void shouldWriteBlank() throws Exception {
        String actual = new ArchitectureUpdateObjectMapper().writeValueAsString(ArchitectureUpdate.blank());
        String expected = getBlankYamlText();
        assertThat(actual.trim(), equalTo(expected.trim()));
    }

    @Test
    public void shouldWriteBlankYamlWithOverriddenName() throws Exception {
        String actual = new ArchitectureUpdateObjectMapper().writeValueAsString(ArchitectureUpdate.builderPreFilledWithBlanks().name("OVERRIDDEN").build());
        String expected = getBlankYamlText().replace("'[SAMPLE NAME]'", "OVERRIDDEN");
        assertThat(actual.trim(), equalTo(expected.trim()));
    }

    private String getBlankYamlText() {
        return String.join("\n"
                , ""
                , "name: '[SAMPLE NAME]'"
                , "milestone: '[SAMPLE MILESTONE]'"
                , "authors:"
                , "- name: '[SAMPLE PERSON NAME]'"
                , "  email: '[SAMPLE PERSON EMAIL]'"
                , "PCAs:"
                , "- name: '[SAMPLE PERSON NAME]'"
                , "  email: '[SAMPLE PERSON EMAIL]'"
                , "P2:"
                , "  link: '[SAMPLE LINK TO P1]'"
                , "  jira:"
                , "    ticket: '[SAMPLE JIRA TICKET]'"
                , "    link: '[SAMPLE JIRA TICKET LINK]'"
                , "P1:"
                , "  link: '[SAMPLE LINK TO P1]'"
                , "  jira:"
                , "    ticket: '[SAMPLE JIRA TICKET]'"
                , "    link: '[SAMPLE JIRA TICKET LINK]'"
                , "  executive-summary: '[SAMPLE EXECUTIVE SUMMARY]'"
                , "useful-links:"
                , "- description: '[SAMPLE LINK DESCRIPTION]'"
                , "  link: '[SAMPLE-LINK]'"
                , "milestone-dependencies:"
                , "- description: '[SAMPLE MILESTONE DEPENDENCY]'"
                , "  links:"
                , "  - description: '[SAMPLE LINK DESCRIPTION]'"
                , "    link: '[SAMPLE-LINK]'"
                , "requirements:"
                , "  '[SAMPLE-REQUIREMENT-ID]':"
                , "    text: '[SAMPLE REQUIREMENT TEXT]'"
                , "    tdd-references:"
                , "    - '[SAMPLE-TDD-ID]'"
                , "TDDs:"
                , "  Component-[SAMPLE-COMPONENT-ID]:"
                , "  - id: '[SAMPLE-TDD-ID]'"
                , "    text: '[SAMPLE TDD TEXT]'"
                , "E2Es:"
                , "- '[SAMPLE E2E]'"
                , "epic:"
                , "  title: '[SAMPLE EPIC TITLE]'"
                , "  jira:"
                , "    ticket: '[SAMPLE JIRA TICKET]'"
                , "    link: '[SAMPLE JIRA TICKET LINK]'"
                , "  capabilities:"
                , "  - jira:"
                , "      ticket: '[SAMPLE JIRA TICKET]'"
                , "      link: '[SAMPLE JIRA TICKET LINK]'"
                , "    tdd-references:"
                , "    - '[SAMPLE-TDD-ID]'"
                , "    requirement-references:"
                , "    - '[SAMPLE-REQUIREMENT-ID]'"
        );
    }
}
