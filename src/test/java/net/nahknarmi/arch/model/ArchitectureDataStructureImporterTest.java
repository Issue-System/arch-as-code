package net.nahknarmi.arch.model;


import org.junit.Test;

import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;


public class ArchitectureDataStructureImporterTest {
    private static final String PRODUCT_NAME = "TestSpaces";

    @Test
    public void should_load_architecture_data_structure_from_yaml_file() {
        InputStream inputStream = getClass().getResourceAsStream("/architecture/products/testspaces/data-structure.yml");
        ArchitectureDataStructure dataStructure = new ArchitectureDataStructureImporter().load(inputStream);

        assertNotNull(dataStructure);
        assertThat(dataStructure.getName(), is(equalTo(PRODUCT_NAME)));
        assertThat(dataStructure.getId(), is(equalTo(49344L)));
        assertThat(dataStructure.getBusinessUnit(), is(equalTo("DevFactory")));
        assertThat(dataStructure.getDescription(), is(equalTo("TestSpaces is a tool!")));


        assertThat(dataStructure.getDecisions().size(), is(equalTo(2)));
        assertThat(dataStructure.getDecisions().get(0).getTitle(), is(equalTo("Docker as the containerization technology platform")));
        assertThat(decisionDate(dataStructure.getDecisions().get(0)), is(equalTo(LocalDate.of(2018, 11, 29))));
        assertThat(dataStructure.getDecisions().get(1).getTitle(), is(equalTo("Kubernetes as the container management platform")));
        assertThat(decisionDate(dataStructure.getDecisions().get(1)), is(equalTo(LocalDate.of(2019, 11, 19))));
    }

    private LocalDate decisionDate(ImportantTechnicalDecision decision) {
        return Instant.ofEpochMilli(decision.getDate().getTime())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
    }
}
