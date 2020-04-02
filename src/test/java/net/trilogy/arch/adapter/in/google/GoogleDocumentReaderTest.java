package net.trilogy.arch.adapter.in.google;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.services.docs.v1.model.Document;
import net.trilogy.arch.domain.ArchitectureUpdate;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GoogleDocumentReaderTest {


    private final GoogleDocsApiInterface mockedApiInterface = mock(GoogleDocsApiInterface.class);
    private final GoogleDocumentReader reader = new GoogleDocumentReader(mockedApiInterface);

    @Test
    public void shouldReturnEmptyAu() throws IOException {
        var apiResponse = new GoogleDocsApiInterface.Response(
                getJsonNodeFrom("{}"),
                new Document()
        );

        mockApiToReturnAGivenB(apiResponse, "url");

        assertThat(reader.load("url"), equalTo(ArchitectureUpdate.blank()));
    }

    @Test
    public void shouldReturnAuWithP1JiraTicket() throws IOException {
        mockApiWith("Json/SampleP1.json", "url");

        ArchitectureUpdate result = reader.load("url");

        assertThat(result.getP1().getJira().getTicket(), equalTo("ABCD-1231"));
    }

    @Test
    public void shouldReturnAuWithMilestone() throws IOException {
        mockApiWith("Json/SampleP1.json", "url");

        ArchitectureUpdate result = reader.load("url");

        assertThat(result.getMilestone(), equalTo("M1.0 First Milestone"));
    }

    private void mockApiWith(String fileWhoseContentsWillBeReturned, String whenCalledWithUrl) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        final Path path = Paths.get(classLoader.getResource(fileWhoseContentsWillBeReturned).getPath());
        final JsonNode sampleSpec = getJsonNodeFrom(Files.readString(path));

        mockApiToReturnAGivenB(
                new GoogleDocsApiInterface.Response(sampleSpec, null),
                whenCalledWithUrl
        );
    }

    @Test
    @Ignore("This is not a test. Use this to generate new json from google docs if needed.")
    public void fetchSampleP1Spec() throws GeneralSecurityException, IOException {
        String url = "https://docs.google.com/document/d/1xPIrv159vlRKklTABSxJx9Yq76MOrRfEdKLiVlXUQ68";

        GoogleDocsApiInterface.Response response = new GoogleDocsApiInterface(
                new GoogleDocsAuthorizedApiFactory(".arch-as-code/google/client_secret.json", ".arch-as-code/google/")
        ).getDocument(url);

        Path tempFile = Files.createTempFile("arch-as-code_test-json-file", ".json");

        new ObjectMapper().writeValue(tempFile.toFile(), response.asJson());

        assertThat(
                "Written to file " + tempFile.toAbsolutePath() + "\nRun: mv " + tempFile.toAbsolutePath() + " src/test/resources/Json/SampleP1.json" + "\nNow failing test on purpuse :)",
                true, is(false)
        );

    }

    private JsonNode getJsonNodeFrom(String content) throws JsonProcessingException {
        return new ObjectMapper().readValue(content, JsonNode.class);
    }

    private void mockApiToReturnAGivenB(GoogleDocsApiInterface.Response a, String b) throws IOException {
        when(mockedApiInterface.getDocument(b)).thenReturn(a);
    }
}
