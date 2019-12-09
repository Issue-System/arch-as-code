package net.nahknarmi.arch.model;


import org.junit.Test;

import java.io.InputStream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;


public class ArchitectureDataStructureImporterTest {

    @Test
    public void should_load_architecture_data_structure_from_yaml_file() {
        InputStream inputStream = getClass().getResourceAsStream("/dev-spaces-architecture.yml");
        ArchitectureDataStructure dataStructure = new ArchitectureDataStructureImporter().load(inputStream);

        assertNotNull(dataStructure);
        assertThat(dataStructure.getName(), is(equalTo("DevSpaces")));
        assertThat(dataStructure.getId(), is(equalTo(49328)));
        assertThat(dataStructure.getBusinessUnit(), is(equalTo("DevFactory")));
    }
}