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

import static net.trilogy.arch.TestHelper.MANIFEST_PATH_TO_TEST_EVERYTHING_EXCEPT_VIEWS;
import static net.trilogy.arch.TestHelper.MANIFEST_PATH_TO_TEST_VIEWS;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class ArchitectureDataStructureReaderTest {

    private final File fileForViews = new File(getClass().getResource(MANIFEST_PATH_TO_TEST_VIEWS).getPath());
    private final File fileForEverythingExceptViews = new File(getClass().getResource(MANIFEST_PATH_TO_TEST_EVERYTHING_EXCEPT_VIEWS).getPath());

    @Test
    public void shouldReadMetaData() throws IOException {
        var data = new ArchitectureDataStructureReader().load(fileForEverythingExceptViews);

        assertThat(data.getName(), is(equalTo("TestSpaces")));
        assertThat(data.getBusinessUnit(), is(equalTo("DevFactory")));
        assertThat(data.getDescription(), is(equalTo("TestSpaces is a tool!")));
    }

    @Test
    public void shouldReadDecisions() throws IOException {
        var data = new ArchitectureDataStructureReader().load(fileForEverythingExceptViews);

        assertThat(data.getDecisions().size(), is(equalTo(2)));

        var firstDecision = data.getDecisions().get(0);
        assertThat(firstDecision.getTitle(), is(equalTo("Docker as the containerization technology platform")));
        assertThat(decisionDate(firstDecision), is(equalTo(LocalDate.of(2018, 11, 29))));

        var secondDecision = data.getDecisions().get(1);
        assertThat(secondDecision.getTitle(), is(equalTo("Kubernetes as the container management platform")));
        assertThat(decisionDate(secondDecision), is(equalTo(LocalDate.of(2019, 11, 19))));
    }

    @Test
    public void shouldReadPeople() throws IOException {
        var data = new ArchitectureDataStructureReader().load(fileForEverythingExceptViews);

        assertThat(data.getModel().getPeople().size(), equalTo(4));

        var actualPerson = data.getModel().findPersonByName("PCA");
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
        var data = new ArchitectureDataStructureReader().load(fileForEverythingExceptViews);

        assertThat(data.getModel().getSystems().size(), is(equalTo(5)));

        var actual = (C4SoftwareSystem) data.getModel().findEntityById("6");
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
    public void shouldReadContainers() throws IOException {
        var data = new ArchitectureDataStructureReader().load(fileForEverythingExceptViews);

        assertThat(data.getModel().getComponents().size(), is(equalTo(4)));

        var actual = (C4Container) data.getModel().findEntityById("11");
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

    @Test
    public void shouldReadComponents() throws IOException {
        var data = new ArchitectureDataStructureReader().load(fileForEverythingExceptViews);

        assertThat(data.getModel().getComponents().size(), is(equalTo(4)));

        var actual = (C4Component) data.getModel().findEntityById("38");
        var expected = C4Component.builder()
                .alias("c4://DevSpaces/DevSpaces API/Sign In Controller")
                .id("38")
                .name("DevSpaces/DevSpaces API/Sign In Controller")
                .containerAlias("c4://DevSpaces/DevSpaces API")
                .description("Allows users to sign in")
                .technology("Spring MVC Rest Controller")
                .url("https://devspaces.io/sign-in")
                .tags(Set.of(new C4Tag("DevSpaces API Component View")))
                .relationships(List.of(
                        new C4Relationship("34", null, C4Action.USES, "c4://DevSpaces/DevSpaces API/Security Component", "14", "Authorizes user", null)
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
