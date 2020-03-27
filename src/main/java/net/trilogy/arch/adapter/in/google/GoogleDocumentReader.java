package net.trilogy.arch.adapter.in.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.docs.v1.Docs;
import com.google.api.services.docs.v1.DocsScopes;
import com.google.api.services.docs.v1.model.Document;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class GoogleDocumentReader {

    private final String clientSecretsPath;
    private final String googleCredsDataStoreDirectory;
    private final NetHttpTransport httpTransport;
    private final JacksonFactory jsonFactory;

    @SneakyThrows
    public GoogleDocumentReader() {
        clientSecretsPath = ".arch-as-code/google/client_secret.json";
        googleCredsDataStoreDirectory = ".arch-as-code/google/stored-credentials";
        httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        jsonFactory = JacksonFactory.getDefaultInstance();
    }

    public void doAThing() {
        System.out.println("\n\n\n***\n\n\n");
        System.out.println("BEGIN");
        execute();
        System.out.println("END");
        System.out.println("\n\n\n***\n\n\n");
    }

    @SneakyThrows
    private void execute() {
        Credential credential = authorize();
        Docs docs = new Docs(
                httpTransport,
                jsonFactory,
                credential
        );
        Document execute = docs.documents().get("1roBAWDEcIVQC4pv1gBGC3GOb8v0Nk7l6pfOIWxbidfA").execute();
        String document = execute.toPrettyString();
        Path file = Files.createTempFile("arch-as-code_google-doc-output", ".json");
        Files.writeString(file, document);

        System.out.println("WRITTEN: " + file.toAbsolutePath());
        System.out.println("Run: cat " + file.toAbsolutePath() + " | jq -c '.body.content[].paragraph.elements[]?.textRun.content'" );

    }

    private Credential authorize() throws Exception {
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                jsonFactory,
                new FileReader(clientSecretsPath)
        );

        GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport,
                jsonFactory,
                clientSecrets.getDetails().getClientId(),
                clientSecrets.getDetails().getClientSecret(),
                List.of(DocsScopes.DOCUMENTS_READONLY)
        ).setCredentialDataStore(
                new FileDataStoreFactory(new File(googleCredsDataStoreDirectory)).getDataStore("user")
        ).build();

        AuthorizationCodeInstalledApp authorizationCodeInstalledApp = new AuthorizationCodeInstalledApp(
                googleAuthorizationCodeFlow,
                new LocalServerReceiver()
        );

        return authorizationCodeInstalledApp.authorize("user");
    }
}
