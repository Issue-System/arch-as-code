package net.trilogy.arch.adapter.in.google;

import com.google.api.services.docs.v1.Docs;
import com.google.api.services.docs.v1.model.Document;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.mockito.Mockito.*;

@RunWith(JUnitParamsRunner.class)
public class GoogleDocumentReaderTest {
    private Docs mockedApi;
    private GoogleDocumentReader reader;

    @Before
    public void setUp() throws IOException {
        mockedApi = mock(Docs.class);
        GoogleDocsAuthorizedApiFactory mockedApiFactory = mock(GoogleDocsAuthorizedApiFactory.class);
        when(mockedApiFactory.getAuthorizedDocsApi()).thenReturn(mockedApi);
        reader = new GoogleDocumentReader(mockedApiFactory);
    }

    @Parameters({"", " ", " \n ", "https://docs.fake.com/document/d/"})
    @Test(expected = GoogleDocumentReader.InvalidUrlException.class)
    public void shouldRaiseExceptionOnEmptyUrl(String s) throws IOException {
        reader.load(s);
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
        mockApi(new Document(), id);

        reader.load(url);

        verify(mockedApi.documents()).get(id);
    }

    private void mockApi(Document toReturn, String given) throws IOException {
        var mockedDocuments = mock(Docs.Documents.class);
        when(mockedApi.documents()).thenReturn(mockedDocuments);

        var mockedGet = mock(Docs.Documents.Get.class);
        when(mockedDocuments.get(given)).thenReturn(mockedGet);

        when(mockedGet.execute()).thenReturn(toReturn);
    }
}
