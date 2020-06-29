package net.trilogy.arch.adapter.out;

import lombok.SneakyThrows;
import net.trilogy.arch.adapter.architectureYaml.ArchitectureDataStructureObjectMapper;
import net.trilogy.arch.adapter.architectureYaml.ArchitectureDataStructureWriter;
import net.trilogy.arch.domain.ArchitectureDataStructure;
import net.trilogy.arch.domain.DocumentationSection;
import net.trilogy.arch.facade.FilesFacade;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        final ArchitectureDataStructure dataStructure = new ArchitectureDataStructureObjectMapper().readValue(
                new FilesFacade().readString(existingYamlFile.toPath())
        );
        File writtenYamlFile = new ArchitectureDataStructureWriter().export(dataStructure);

        extractDates(writtenYamlFile).forEach(this::parseDateAsIsoOrThrow);
    }

    @Test
    public void shouldWriteTheSameYamlAsWhatWasRead() throws Exception {
        File existingYamlFile = new File(getClass().getResource(MANIFEST_PATH_TO_TEST_MODEL_DEPLOYMENT_NODES).getPath());
        final ArchitectureDataStructure dataStructure = new ArchitectureDataStructureObjectMapper().readValue(
                new FilesFacade().readString(existingYamlFile.toPath())
        );
        File writtenYamlFile = new ArchitectureDataStructureWriter().export(dataStructure);

        assertYamlContentsEqual(writtenYamlFile, existingYamlFile);
    }

    @Test
    public void shouldWriteYamlToSpecifiedDirectory() throws Exception {
        final File tempFile = File.createTempFile("aac", "test");

        File writtenYamlFile = new ArchitectureDataStructureWriter()
                .export(new ArchitectureDataStructure(), tempFile);

        assertThat(tempFile.getAbsoluteFile(), equalTo(writtenYamlFile.getAbsoluteFile()));
    }

    @Test
    public void shouldWriteDocumentationToCorrectLocation() throws Exception {
        // Given
        final Path rootDir = Files.createTempDirectory("aacDir");
        final File tempFile = File.createTempFile("aac", "test", rootDir.toFile());
        final String content = "##Content";
        ArchitectureDataStructure arch = getArchWithDocumentation(content);

        // When
        final File exportedYaml = new ArchitectureDataStructureWriter().export(arch, tempFile);
        final Path pathToDoc = Paths.get(exportedYaml.getParent().toString()).resolve("documentation").resolve("DocTitle.md");
        String docAsString = Files.readString(pathToDoc);

        // Then
        assertThat(docAsString, equalTo(content));
    }

    @Test
    public void shouldNotComplainIfDocumentationDirectoryAlreadyExists() throws Exception {
        // Given
        final Path rootDir = Files.createTempDirectory("aacDir");
        Files.createDirectory(rootDir.resolve("documentation"));  // with existing documentation directory
        final File tempFile = File.createTempFile("aac", "test", rootDir.toFile());
        final String content = "##Content";
        ArchitectureDataStructure arch = getArchWithDocumentation(content);

        // When
        final File exportedYaml = new ArchitectureDataStructureWriter().export(arch, tempFile);
        final Path pathToDoc = Paths.get(exportedYaml.getParent().toString()).resolve("documentation").resolve("DocTitle.md");
        String docAsString = Files.readString(pathToDoc);

        // Then
        assertThat(docAsString, equalTo(content));
    }

    @SneakyThrows
    public void parseDateAsIsoOrThrow(String str) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        df.parse(str);
    }

    private List<String> extractDates(File writtenYamlFile) throws Exception {
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

    private void assertYamlContentsEqual(File actual, File expected) throws Exception {
        final ArchitectureDataStructure actualData = new ArchitectureDataStructureObjectMapper().readValue(
                new FilesFacade().readString(actual.toPath())
        );
        final ArchitectureDataStructure expectedData = new ArchitectureDataStructureObjectMapper().readValue(
                new FilesFacade().readString(expected.toPath())
        );

        assertThat(actualData, is(equalTo(expectedData)));
    }

    private ArchitectureDataStructure getArchWithDocumentation(String content) {
        return ArchitectureDataStructure.builder()
                .name("name")
                .businessUnit("businessUnit")
                .description("description")
                .documentation(List.of(
                        new DocumentationSection("1", "DocTitle", 1, "Markdown", content)
                )).build();
    }

}
