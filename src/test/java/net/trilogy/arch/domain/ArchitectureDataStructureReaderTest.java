package net.trilogy.arch.domain;


import net.trilogy.arch.adapter.in.ArchitectureDataStructureReader;
import net.trilogy.arch.domain.c4.*;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

import static net.trilogy.arch.TestHelper.TEST_SPACES_MANIFEST_PATH;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class ArchitectureDataStructureReaderTest {

    private final ArchitectureDataStructure dataStructureReadFromFile;

    public ArchitectureDataStructureReaderTest() throws IOException {
        File productDocumentationRoot = new File(getClass().getResource(TEST_SPACES_MANIFEST_PATH).getPath());
        this.dataStructureReadFromFile = new ArchitectureDataStructureReader().load(productDocumentationRoot);
    }

    @Test
    public void shouldReadMetaData() {
        assertThat(this.dataStructureReadFromFile.getName(), is(equalTo("TestSpaces")));
        assertThat(this.dataStructureReadFromFile.getBusinessUnit(), is(equalTo("DevFactory")));
        assertThat(this.dataStructureReadFromFile.getDescription(), is(equalTo("TestSpaces is a tool!")));
    }

    @Test
    public void shouldReadDecisions() {
        var decisions = this.dataStructureReadFromFile.getDecisions();
        assertThat(decisions.size(), is(equalTo(2)));

        var firstDecision = decisions.get(0);
        assertThat(firstDecision.getTitle(), is(equalTo("Docker as the containerization technology platform")));
        assertThat(decisionDate(firstDecision), is(equalTo(LocalDate.of(2018, 11, 29))));

        var secondDecision = decisions.get(1);
        assertThat(secondDecision.getTitle(), is(equalTo("Kubernetes as the container management platform")));
        assertThat(decisionDate(secondDecision), is(equalTo(LocalDate.of(2019, 11, 19))));

    }

    @Test
    public void shouldReadPeople() {
        var model = this.dataStructureReadFromFile.getModel();
        var people = model.getPeople();
        assertThat(people.size(), equalTo(4));

        var actualPerson = model.findPersonByName("PCA");
        var expectedPerson = C4Person.builder()
                .alias("@PCA")
                .description("Product Chief Architect")
                .id("3")
                .location(null)
                .name("PCA")
                .tags(Set.of(
                        new C4Tag("Trilogy System View")
                ))
                .relationships(List.of(
                        new C4Relationship("26", null, C4Action.USES, "c4://GitHub", "8", "as a version control system", null),
                        new C4Relationship("27", null, C4Action.USES, "c4://XO Chat", "5", "to communicate with team", null),
                        new C4Relationship("28", null, C4Action.USES, "c4://Trilogy Google G Suite", "7", "inter-team collaboration", null)
                ))
                .path(null)
                .build();

        assertThat(actualPerson, is(equalTo(expectedPerson)));
    }

    @Test
    public void shouldReadSystems() {
        var model = this.dataStructureReadFromFile.getModel();
        var systems = model.getSystems();
        assertThat(systems.size(), is(equalTo(5)));

        var actual = (C4SoftwareSystem) model.findEntityById("6");
        var expected = C4SoftwareSystem.builder()
                .alias("c4://SalesForce")
                .id("6")
                .name("SalesForce")
                .description("Book keeping")
                .location(C4Location.EXTERNAL)
                .relationships(List.of(
                        new C4Relationship("29", null, C4Action.USES, "c4://DevSpaces/DevSpaces Backend", "11", "queries usage details to estimate monthly costs", null)
                ))
                .tags(Set.of())
                .path(null)
                .build();

        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void shouldReadContainers() {
        var model = this.dataStructureReadFromFile.getModel();
        var containers = model.getComponents();
        assertThat(containers.size(), is(equalTo(4)));

        var actual = (C4Container) model.findEntityById("11");
        var expected = C4Container.builder()
                .alias("c4://DevSpaces/DevSpaces Backend")
                .name("DevSpaces/DevSpaces Backend")
                .id("11")
                .systemAlias("c4://DevSpaces")
                .description("Restful API providing capabilities for interacting with a DevSpace")
                .technology("Spring Boot")
                .tags(Set.of(
                        new C4Tag("DevSpaces Container View")
                ))
                .relationships(List.of(
                        new C4Relationship("32", null, C4Action.USES, "c4://DevSpaces/DevSpaces API", "13", "to manipulate dev spaces", null)
                ))
                .path(null)
                .build();

        assertThat(actual, is(equalTo(expected)));
    }

    private LocalDate decisionDate(ImportantTechnicalDecision decision) {
        return Instant.ofEpochMilli(decision.getDate().getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}
