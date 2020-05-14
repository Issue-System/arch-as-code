package net.trilogy.arch.adapter.out;

import lombok.SneakyThrows;
import net.trilogy.arch.adapter.FilesFacade;
import net.trilogy.arch.adapter.in.ArchitectureDataStructureReader;
import net.trilogy.arch.domain.ArchitectureDataStructure;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import static java.util.stream.Collectors.toList;
import static net.trilogy.arch.TestHelper.MANIFEST_PATH_TO_TEST_GENERALLY;
import static net.trilogy.arch.TestHelper.MANIFEST_PATH_TO_TEST_MODEL_DEPLOYMENT_NODES;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ArchitectureDataStructureWriterTest {

    @Test
    public void shouldWriteHumanReadableDates() throws Exception {
        File existingYamlFile = new File(getClass().getResource(MANIFEST_PATH_TO_TEST_GENERALLY).getPath());
        ArchitectureDataStructure dataStructure = new ArchitectureDataStructureReader(new FilesFacade()).load(existingYamlFile);

        File writtenYamlFile = new ArchitectureDataStructureWriter().export(dataStructure);

        extractDates(writtenYamlFile).forEach(this::parseDateAsIsoOrThrow);
    }

    @Test
    public void shouldWriteTheSameYamlAsWhatWasRead() throws Exception {
        File existingYamlFile = new File(getClass().getResource(MANIFEST_PATH_TO_TEST_MODEL_DEPLOYMENT_NODES).getPath());
        ArchitectureDataStructure dataStructure = new ArchitectureDataStructureReader(new FilesFacade()).load(existingYamlFile);

        File writtenYamlFile = new ArchitectureDataStructureWriter().export(dataStructure);

        assertYamlContentsEqual(writtenYamlFile, existingYamlFile);
    }

    @Test
    public void shouldWriteYamlToSpecifiedDirectory() throws IOException {
        final File tempFile = File.createTempFile("aac", "test");

        File writtenYamlFile = new ArchitectureDataStructureWriter()
                .export(new ArchitectureDataStructure(), tempFile);

        assertThat(tempFile.getAbsoluteFile(), equalTo(writtenYamlFile.getAbsoluteFile()));
    }

    @SneakyThrows
    public void parseDateAsIsoOrThrow(String str) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        df.parse(str);
    }

    private List<String> extractDates(File writtenYamlFile) throws IOException {
        return Files.readAllLines(writtenYamlFile.toPath())
                .stream()
                .filter(it -> it.contains("date"))
                .map(String::trim)
                .map(it -> it.replace("date: ", ""))
                .map(this::trimQuotes)
                .collect(toList());
    }

    private String trimQuotes(String s) {
        return s.replaceAll("^\"|\"$", "");
    }

    private void assertYamlContentsEqual(File actual, File expected) throws IOException {
        ArchitectureDataStructure actualData = new ArchitectureDataStructureReader(new FilesFacade()).load(actual);
        ArchitectureDataStructure expectedData = new ArchitectureDataStructureReader(new FilesFacade()).load(expected);
        assertThat(actualData, is(equalTo(expectedData)));
    }

}
