package net.nahknarmi.arch.model;


import com.google.api.client.util.DateTime;
import org.junit.Test;

import java.io.InputStream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;


public class ArchitectureDataStructureImporterTest {

    @Test
    public void should_load_architecture_data_structure_from_yaml_file() {
        InputStream inputStream = getClass().getResourceAsStream("/architecture/products/devspaces/dev-spaces-architecture.yml");
        ArchitectureDataStructure dataStructure = new ArchitectureDataStructureImporter().load(inputStream);

        assertNotNull(dataStructure);
        assertThat(dataStructure.getName(), is(equalTo("DevSpaces")));
        assertThat(dataStructure.getId(), is(equalTo(49328L)));
        assertThat(dataStructure.getBusinessUnit(), is(equalTo("DevFactory")));
        assertThat(dataStructure.getDescription(), is(equalTo("DevFactory is a tool")));

        assertThat(dataStructure.getDecisions().size(), is(equalTo(2)));
        assertThat(dataStructure.getDecisions().get(0).getTitle(), is(equalTo("Docker as the containerization technology platform")));
        assertThat(dataStructure.getDecisions().get(0).getDate(), is(equalTo(new DateTime("2019-11-19T16:04:32.000Z"))));
        assertThat(dataStructure.getDecisions().get(1).getTitle(), is(equalTo("Kubernetes as the container management platform")));
        assertThat(dataStructure.getDecisions().get(1).getDate(), is(equalTo(new DateTime("2019-11-19T19:07:15Z"))));
    }
}
