package net.trilogy.arch.adapter.in.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.docs.v1.Docs;
import com.google.api.services.docs.v1.DocsScopes;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.List;

import static net.trilogy.arch.adapter.in.google.GoogleDocsAuthorizer.CREDS_DATASTORE_NAME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class GoogleDocsAuthorizerTest {
    @Rule
    public final ErrorCollector collector = new ErrorCollector();

    private final String CLIENT_ID = "client-id";
    private final String CLIENT_SECRET = "client-secret";
    private final List<String> SCOPES = List.of(DocsScopes.DOCUMENTS_READONLY);

    private GoogleDocsAuthorizer.AuthorizationCodeInstalledAppFactory mockedAuthorizationCodeInstalledAppFactory;
    private GoogleDocsAuthorizer.CodeFlowBuilderFactory mockedCodeFlowBuilderFactory;
    private GoogleDocsAuthorizer.DocsFactory mockedDocsFactory;
    private Path userCredentialsDirectory;
    private Path clientCredentialsFile;
    private final NetHttpTransport httpTransport = new NetHttpTransport();
    private final JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();

    @Before
    public void setUp() throws GeneralSecurityException, IOException {
        userCredentialsDirectory = Files.createTempDirectory("arch-as-code_tests");
        clientCredentialsFile = Files.createTempFile("arch-as-code_tests", ".json");

        Files.writeString(
                clientCredentialsFile,
                "{" +
                        "\"installed\":" +
                        "{" +
                        "\"client_id\":\"" + CLIENT_ID + "\"," +
                        "\"project_id\":\"proj-id\"," +
                        "\"auth_uri\":\"https://accounts.fake.com/o/oauth2/auth\"," +
                        "\"token_uri\":\"https://oauth2.fakeapis.com/token\"," +
                        "\"auth_provider_x509_cert_url\":\"https://www.fakeapis.com/oauth2/v1/certs\"," +
                        "\"client_secret\":\"" + CLIENT_SECRET + "\"," +
                        "\"redirect_uris\":[\"urn:ietf:wg:oauth:2.0:oob\",\"http://localhost\"]" +
                        "}" +
                        "}"
        );

        mockedAuthorizationCodeInstalledAppFactory = Mockito.mock(GoogleDocsAuthorizer.AuthorizationCodeInstalledAppFactory.class);
        mockedCodeFlowBuilderFactory = Mockito.mock(GoogleDocsAuthorizer.CodeFlowBuilderFactory.class);
        mockedDocsFactory = Mockito.mock(GoogleDocsAuthorizer.DocsFactory.class);
    }

    @Test
    public void shouldUseCorrectAuFolder() {
        collector.checkThat(CREDS_DATASTORE_NAME, equalTo("userCredentialsDatastore"));
    }

    @Test
    public void shouldCallTheGoogleApiInTheCorrectSequence() throws IOException {
        // GIVEN
        Docs expected = mockGoogleApiBehavior();

        GoogleDocsAuthorizer authorizer = new GoogleDocsAuthorizer(
                clientCredentialsFile.toAbsolutePath().toString(),
                userCredentialsDirectory.toAbsolutePath().toString(),
                httpTransport,
                jsonFactory,
                mockedCodeFlowBuilderFactory,
                mockedAuthorizationCodeInstalledAppFactory,
                mockedDocsFactory);

        // WHEN
        var result = authorizer.getAuthorizedDocsApi();

        // THEN
        assertThat(result, is(expected));
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    private Docs mockGoogleApiBehavior() throws IOException {
        var googleCodeFlowResult = mockGoogleCodeFlow();
        var googleAuthorizeFlowResult = mockGoogleAuthorizeFlow(googleCodeFlowResult);
        var googleDocsFlowResult = mockGoogleDocsFlow(googleAuthorizeFlowResult);
        return googleDocsFlowResult;
    }

    private Docs mockGoogleDocsFlow(Credential googleAuthorizeFlowResult) {
        var mockedDocsApi = Mockito.mock(Docs.class);
        Mockito.when(mockedDocsFactory.make(httpTransport, jsonFactory, googleAuthorizeFlowResult)).thenReturn(mockedDocsApi);
        return mockedDocsApi;
    }

    private Credential mockGoogleAuthorizeFlow(GoogleAuthorizationCodeFlow googleCodeFlowResult) throws IOException {
        var mockedAuthorization = Mockito.mock(AuthorizationCodeInstalledApp.class);
        Mockito.when(
                mockedAuthorizationCodeInstalledAppFactory.make(same(googleCodeFlowResult), any())
        ).thenReturn(
                mockedAuthorization
        );

        var mockedCredential = Mockito.mock(Credential.class);
        Mockito.when(mockedAuthorization.authorize(CREDS_DATASTORE_NAME)).thenReturn(mockedCredential);
        return mockedCredential;
    }

    private GoogleAuthorizationCodeFlow mockGoogleCodeFlow() throws IOException {
        var mockedCodeFlowBuilder = Mockito.mock(GoogleAuthorizationCodeFlow.Builder.class);
        // TODO Future: Replace any() with factory
        Mockito.when(mockedCodeFlowBuilder.setCredentialDataStore(any())).thenReturn(mockedCodeFlowBuilder);
        Mockito.when(
                mockedCodeFlowBuilderFactory.make(httpTransport, jsonFactory, CLIENT_ID, CLIENT_SECRET, SCOPES)
        ).thenReturn(
                mockedCodeFlowBuilder
        );

        var mockedCodeFlow = Mockito.mock(GoogleAuthorizationCodeFlow.class);
        Mockito.when(mockedCodeFlowBuilder.build()).thenReturn(mockedCodeFlow);
        return mockedCodeFlow;
    }

}
