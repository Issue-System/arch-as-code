package net.trilogy.arch.adapter.in.google;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.api.services.docs.v1.Docs;
import com.google.api.services.docs.v1.model.Body;
import com.google.api.services.docs.v1.model.Document;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

@RunWith(JUnitParamsRunner.class)
public class GoogleDocsApiInterfaceTest {
    private Docs mockedApi;
    private GoogleDocsApiInterface apiInterface;

    @Before
    public void setUp() throws IOException {
        mockedApi = mock(Docs.class);
        GoogleDocsAuthorizedApiFactory mockedApiFactory = mock(GoogleDocsAuthorizedApiFactory.class);
        when(mockedApiFactory.getAuthorizedDocsApi()).thenReturn(mockedApi);
        apiInterface = new GoogleDocsApiInterface(mockedApiFactory);
    }

    @Parameters({"", " ", " \n ", "https://docs.fake.com/document/d/"})
    @Test(expected = GoogleDocsApiInterface.InvalidUrlException.class)
    public void shouldRaiseExceptionOnEmptyUrl(String url) throws IOException {
        apiInterface.getDocument(url);
    }

    @Parameters({
            "https://docs.fake.com/document/d/1yTTKKPfZzf6Q6h4IBxT1u_-DrarilQnvpNCp6LRTlfk/view | 1yTTKKPfZzf6Q6h4IBxT1u_-DrarilQnvpNCp6LRTlfk",
            "https://docs.fake.com/document/d/1yTTKKPfZzf6Q6h4IBxT1u_-DrarilQnvpNCp6LRTlfk/edit?usp=sharing | 1yTTKKPfZzf6Q6h4IBxT1u_-DrarilQnvpNCp6LRTlfk",
            "docs.fake.com/document/d/1yTTKKPfZzf6Q6h4IBxT1u_-DrarilQnvpNCp6LRTlfk/view | 1yTTKKPfZzf6Q6h4IBxT1u_-DrarilQnvpNCp6LRTlfk",
            "docs.fake.com/document/d/1yTTKKPfZzf6Q6h4IBxT1u_-DrarilQnvpNCp6LRTlfk | 1yTTKKPfZzf6Q6h4IBxT1u_-DrarilQnvpNCp6LRTlfk",
            "https://docs.fake.com/document/d/1yTTKKPfZzf6Q6h4IBxT1u_-DrarilQnvpNCp6LRTlfk | 1yTTKKPfZzf6Q6h4IBxT1u_-DrarilQnvpNCp6LRTlfk",
            "docs.fake.com/document/d/1yTTKKPfZzf6Q6h4IBxT1u_-DrarilQnvpNCp6LRTlfk | 1yTTKKPfZzf6Q6h4IBxT1u_-DrarilQnvpNCp6LRTlfk",
    })
    @Test
    public void shouldParseDocumentId(String url, String id) throws IOException {
        mockApiToReturn(new Document(), id);

        apiInterface.getDocument(url);

        verify(mockedApi.documents()).get(id);
    }

    @Test
    public void shouldReturnParsedJson() throws IOException {
        String id = "1yTTKKPfZzf6Q6h4IBxT1u_-DrarilQnvpNCp6LRTlfk";
        String url = "https://docs.fake.com/document/d/" + id;

        //given that the api returns a new document
        final Document mockedDoc = new Document();
        mockedDoc.setBody(new Body());
        mockApiToReturn(mockedDoc, id);

        //when we call our interface
        JsonNode result = apiInterface.getDocument(url);

        //the interface should return json, not the new Document() that the api returned
        assertThat(result.get("body").toString(), equalTo("{}"));
    }

    private void mockApiToReturn(Document toReturn, String given) throws IOException {
        var mockedDocuments = mock(Docs.Documents.class);
        when(mockedApi.documents()).thenReturn(mockedDocuments);

        var mockedGet = mock(Docs.Documents.Get.class);
        when(mockedDocuments.get(given)).thenReturn(mockedGet);

        when(mockedGet.execute()).thenReturn(toReturn);
    }
}
