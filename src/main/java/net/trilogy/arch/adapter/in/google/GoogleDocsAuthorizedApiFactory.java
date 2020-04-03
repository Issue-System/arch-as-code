package net.trilogy.arch.adapter.in.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.docs.v1.Docs;
import com.google.api.services.docs.v1.DocsScopes;
import com.google.common.annotations.VisibleForTesting;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class GoogleDocsAuthorizedApiFactory {

    public static final String CREDS_DATASTORE_NAME = "userCredentialsDatastore";

    private final String clientCredentialsPath;
    private final String userCredentialsDirectory;
    private final NetHttpTransport httpTransport;
    private final JacksonFactory jsonFactory;
    private final CodeFlowBuilderFactory codeFlowBuilderFactory;
    private final AuthorizationCodeInstalledAppFactory authorizationCodeInstalledAppFactory;
    private final DocsFactory docsFactory;

    public GoogleDocsAuthorizedApiFactory(
            String clientCredentialsPath,
            String userCredentialsDirectory
    ) throws GeneralSecurityException, IOException {
        this.clientCredentialsPath = clientCredentialsPath;
        this.userCredentialsDirectory = userCredentialsDirectory;
        this.httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        this.jsonFactory = JacksonFactory.getDefaultInstance();
        this.docsFactory = new DocsFactory();
        this.codeFlowBuilderFactory = new CodeFlowBuilderFactory();
        this.authorizationCodeInstalledAppFactory = new AuthorizationCodeInstalledAppFactory();
    }

    @VisibleForTesting
    public GoogleDocsAuthorizedApiFactory(
            String clientCredentialsPath,
            String userCredentialsDirectory,
            NetHttpTransport httpTransport,
            JacksonFactory jsonFactory,
            CodeFlowBuilderFactory codeFlowBuilderFactory,
            AuthorizationCodeInstalledAppFactory authorizationCodeInstalledAppFactory,
            DocsFactory docsFactory
    ) {
        this.clientCredentialsPath = clientCredentialsPath;
        this.userCredentialsDirectory = userCredentialsDirectory;
        this.httpTransport = httpTransport;
        this.jsonFactory = jsonFactory;
        this.codeFlowBuilderFactory = codeFlowBuilderFactory;
        this.authorizationCodeInstalledAppFactory = authorizationCodeInstalledAppFactory;
        this.docsFactory = docsFactory;
    }

    public GoogleDocsApiInterface getAuthorizedDocsApi() throws IOException {
        Docs rawApi = docsFactory.make(httpTransport, jsonFactory, authorize());
        return new GoogleDocsApiInterface(rawApi);
    }

    private Credential authorize() throws IOException {
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, new FileReader(clientCredentialsPath));
        GoogleAuthorizationCodeFlow.Builder codeFlowBuilder = codeFlowBuilderFactory.make(
                httpTransport,
                jsonFactory,
                clientSecrets.getDetails().getClientId(),
                clientSecrets.getDetails().getClientSecret(),
                List.of(DocsScopes.DOCUMENTS_READONLY)
        );
        DataStore<StoredCredential> dataStore = new FileDataStoreFactory(new File(userCredentialsDirectory)).getDataStore(CREDS_DATASTORE_NAME);
        GoogleAuthorizationCodeFlow codeFlow = codeFlowBuilder.setCredentialDataStore(dataStore).build();
        AuthorizationCodeInstalledApp authorizationCodeInstalledApp = authorizationCodeInstalledAppFactory.make(codeFlow, new LocalServerReceiver());
        return authorizationCodeInstalledApp.authorize(CREDS_DATASTORE_NAME);
    }

    @VisibleForTesting
    static class DocsFactory {
        public Docs make(NetHttpTransport httpTransport, JacksonFactory jsonFactory, Credential credential) {
            return new Docs(
                    httpTransport,
                    jsonFactory,
                    credential
            );
        }
    }

    @VisibleForTesting
    static class AuthorizationCodeInstalledAppFactory {
        public AuthorizationCodeInstalledApp make(GoogleAuthorizationCodeFlow codeFlow, VerificationCodeReceiver receiver) {
            return new AuthorizationCodeInstalledApp(codeFlow, receiver);
        }
    }

    @VisibleForTesting
    static class CodeFlowBuilderFactory {
        public GoogleAuthorizationCodeFlow.Builder make(
                NetHttpTransport httpTransport,
                JacksonFactory jsonFactory,
                String clientId,
                String clientSecret,
                List<String> scopes
        ) {
            return new GoogleAuthorizationCodeFlow.Builder(
                    httpTransport,
                    jsonFactory,
                    clientId,
                    clientSecret,
                    scopes
            );
        }
    }
}
