package net.trilogy.arch.adapter.out;

import net.trilogy.arch.adapter.in.ArchitectureDataStructureReader;
import net.trilogy.arch.domain.ArchitectureDataStructure;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static net.trilogy.arch.TestHelper.TEST_SPACES_MANIFEST_PATH;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ArchitectureDataStructureWriterTest {

    @Test
    public void shouldWriteTheSameYamlAsWhatWasRead() throws IOException {
        File existingYamlFile = new File(getClass().getResource(TEST_SPACES_MANIFEST_PATH).getPath());
        ArchitectureDataStructure dataStructure = new ArchitectureDataStructureReader().load(existingYamlFile);

        File writtenYamlFile = new ArchitectureDataStructureWriter().export(dataStructure);

        assertYamlContentsEqual(writtenYamlFile, existingYamlFile);
    }

    private void assertYamlContentsEqual(File actual, File expected) throws IOException {
        ArchitectureDataStructure actualData = new ArchitectureDataStructureReader().load(actual);
        ArchitectureDataStructure expectedData = new ArchitectureDataStructureReader().load(expected);
        assertThat(actualData, is(equalTo(expectedData)));
    }

}
