package net.trilogy.arch.adapter.in.google;

import com.google.api.services.docs.v1.Docs;
import com.google.api.services.docs.v1.model.Document;
import net.trilogy.arch.domain.ArchitectureUpdate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mockito;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class GoogleDocumentReaderTest {
    private Docs mockDocApi;
    private GoogleDocumentReader reader;

    @Before
    public void setUp() throws IOException {
        GoogleDocsAuthorizer authorizer = Mockito.mock(GoogleDocsAuthorizer.class);
        mockDocApi = Mockito.mock(Docs.class, Answers.RETURNS_DEEP_STUBS);
        Mockito.when(authorizer.getAuthorizedDocsApi())
                .thenReturn(mockDocApi);

        reader = new GoogleDocumentReader(authorizer);
    }


    @Test(expected = GoogleDocumentReader.InvalidUrlException.class)
    public void shouldRaiseExceptionOnEmptyUrl() {
        reader.load("  ");
    }

    @Test
    public void shouldCreateBlankAuWhenDocIsBlank() throws IOException {
        Document doc = new Document();
        String url = "http://url";
        mockApi(doc, url);

        ArchitectureUpdate expected = ArchitectureUpdate.blank();
        ArchitectureUpdate actual = reader.load(url);
        assertThat(expected, equalTo(actual));
    }

    private void mockApi(Document d, String url) throws IOException {
        Mockito.when(mockDocApi.documents().get(url).execute())
                .thenReturn(d);
    }
}
