package net.trilogy.arch.adapter.in.google;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.services.docs.v1.model.Document;
import net.trilogy.arch.domain.ArchitectureUpdate;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Objects;

import static net.trilogy.arch.adapter.in.google.GoogleDocsAuthorizedApiFactory.GOOGLE_DOCS_API_CREDENTIALS_FOLDER_PATH;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GoogleDocumentReaderTest {


    private final GoogleDocsApiInterface mockedApiInterface = mock(GoogleDocsApiInterface.class);
    private final GoogleDocumentReader reader = new GoogleDocumentReader(mockedApiInterface);

    @Test
    public void shouldReturnEmptyAu() throws Exception {
        var apiResponse = new GoogleDocsApiInterface.Response(
                getJsonNodeFrom("{}"),
                new Document()
        );

        mockApiToReturnAGivenB(apiResponse, "url");

        assertThat(reader.load("url"), equalTo(ArchitectureUpdate.blank()));
    }

    @Test
    public void shouldReturnAuWithExecutiveSummary() throws Exception {
        mockApiWith("Json/SampleP1-1.json", "url");

        ArchitectureUpdate result = reader.load("url");

        String expected = "Just a whole bunch of text goes here. Like a bunch of it. " +
                "Maybe some symbols &@#*!)(@(#&*@." +
                "Maybe some paragraphs. " +
                "Just a bunch, yknow?";

        assertThat(result.getP1().getSummary(), equalTo(expected));
    }

    @Test
    public void shouldReturnAuWithP2Link() throws Exception {
        mockApiWith("Json/SampleP1-1.json", "url");

        ArchitectureUpdate result = reader.load("url");

        assertThat(result.getP2().getLink(), equalTo("http://fake-link-to-p2.com"));
    }

    @Test
    public void shouldReturnAuWithP1Link() throws Exception {
        mockApiWith("Json/SampleP1-1.json", "url");

        ArchitectureUpdate result = reader.load("url");

        assertThat(result.getP1().getLink(), equalTo("url"));
    }

    @Test
    public void shouldReturnAuWithP1JiraTicket() throws Exception {
        mockApiWith("Json/SampleP1-1.json", "url");

        ArchitectureUpdate result = reader.load("url");

        assertThat(result.getP1().getJira().getTicket(), equalTo("ABCD-1231"));
        assertThat(result.getP1().getJira().getLink(), equalTo("http://fake-jira.com"));
    }

    @Test
    public void shouldReturnAuWithMilestone() throws Exception {
        mockApiWith("Json/SampleP1-1.json", "url");

        ArchitectureUpdate result = reader.load("url");

        assertThat(result.getMilestone(), equalTo("M1.0 First Milestone"));
    }

    @SuppressWarnings("SameParameterValue")
    private void mockApiWith(String fileWhoseContentsWillBeReturned, String whenCalledWithUrl) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        final Path path = Paths.get(
                Objects.requireNonNull(
                        classLoader.getResource(fileWhoseContentsWillBeReturned)
                ).getPath()
        );
        final JsonNode sampleSpec = getJsonNodeFrom(Files.readString(path));

        mockApiToReturnAGivenB(
                new GoogleDocsApiInterface.Response(sampleSpec, null),
                whenCalledWithUrl
        );
    }

    // TODO: Remove when no longer needed
    @Test
    @Ignore("This is not a test. Use this to generate new json from google docs if needed.")
    public void NotATest_UtilToFetchSampleP1Spec() throws GeneralSecurityException, IOException {
        String url1 = "https://docs.google.com/document/d/1xPIrv159vlRKklTABSxJx9Yq76MOrRfEdKLiVlXUQ68";
        String url2 = "https://docs.google.com/document/d/1Mhli4ZvCAAIwIguE7UY-DihkI1JsdxZjRG36QVen5aU/edit#";
        String url3 = "https://docs.google.com/document/d/1h-yiali65IQp6qXWb6qxkKvwvYTI9oshfOqJ3SmM4jQ/edit#";
        File productDocumentationRoot = new File(".");

        var apiFactory = new GoogleDocsAuthorizedApiFactory();
        var api = apiFactory.getAuthorizedDocsApi(productDocumentationRoot);
        var response1 = api.fetch(url1);
        var response2 = api.fetch(url2);
        var response3 = api.fetch(url3);

        Path tempFile1 = Files.createTempFile("arch-as-code_test-json-file", ".json");
        Path tempFile2 = Files.createTempFile("arch-as-code_test-json-file", ".json");
        Path tempFile3 = Files.createTempFile("arch-as-code_test-json-file", ".json");

        new ObjectMapper().writeValue(tempFile1.toFile(), response1.asJson());
        new ObjectMapper().writeValue(tempFile2.toFile(), response2.asJson());
        new ObjectMapper().writeValue(tempFile3.toFile(), response3.asJson());

        assertThat(
                "********* STATUS *********" +
                        "\n\nReading doc: " + url1 +
                        "\nReading doc: " + url2 +
                        "\nReading doc: " + url3 +
                        "\n\nUsing credentials within " + productDocumentationRoot.toPath().resolve(GOOGLE_DOCS_API_CREDENTIALS_FOLDER_PATH).toAbsolutePath() +
                        "\n\nWritten to file " + tempFile1.toAbsolutePath() +
                        "\nWritten to file " + tempFile2.toAbsolutePath() +
                        "\nWritten to file " + tempFile3.toAbsolutePath() +
                        "\n\nRun: mv " + tempFile1.toAbsolutePath() + " src/test/resources/Json/SampleP1-1.json" +
                        "\nRun: mv " + tempFile2.toAbsolutePath() + " src/test/resources/Json/SampleP1-2.json" +
                        "\nRun: mv " + tempFile3.toAbsolutePath() + " src/test/resources/Json/SampleP1-3.json" +
                        "\n\nNow failing test on purpose :)" +
                        "\n\n********* STATUS *********\n\n",
                true, is(false)
        );

    }

    private JsonNode getJsonNodeFrom(String content) throws JsonProcessingException {
        return new ObjectMapper().readValue(content, JsonNode.class);
    }

    private void mockApiToReturnAGivenB(GoogleDocsApiInterface.Response a, String b) throws IOException {
        when(mockedApiInterface.fetch(b)).thenReturn(a);
    }
}
