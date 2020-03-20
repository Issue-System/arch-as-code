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
        C4Model model = this.dataStructureReadFromFile.getModel();

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
    public void shouldReadSystems() throws IOException {
        C4Model model = this.dataStructureReadFromFile.getModel();
        assertThat(model.getSystems().size(), is(equalTo(5)));
    }

    private LocalDate decisionDate(ImportantTechnicalDecision decision) {
        return Instant.ofEpochMilli(decision.getDate().getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}
