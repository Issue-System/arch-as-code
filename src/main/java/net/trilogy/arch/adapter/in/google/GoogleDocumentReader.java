package net.trilogy.arch.adapter.in.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.docs.v1.DocsScopes;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileReader;
import java.util.List;

public class GoogleDocumentReader {
    public void doAThing() {
        System.out.println("\n\n\n***\n\n\n");
        System.out.println("BEGIN");
        execute();
        System.out.println("END");
        System.out.println("\n\n\n***\n\n\n");
    }

    @SneakyThrows
    private void execute() {
        authorize();
    }

    private static void authorize() throws Exception {
        JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        String clientSecretsPath = ".arch-as-code/google/client_secret.json";
        FileDataStoreFactory fileDataStoreFactory = new FileDataStoreFactory(
                File.createTempFile("prefix", "suffix")
        );

        // load client secrets
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                jsonFactory,
                new FileReader(clientSecretsPath)
        );

        // set up authorization code flow
        GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow = new GoogleAuthorizationCodeFlow(
                httpTransport,
                jsonFactory,
                clientSecrets.getDetails().getClientId(),
                clientSecrets.getDetails().getClientSecret(),
                List.of(DocsScopes.DOCUMENTS_READONLY)
        );

        Credential user = new AuthorizationCodeInstalledApp(
                googleAuthorizationCodeFlow,
                new LocalServerReceiver()
        ).authorize("user");

        System.out.println(user);

    }
}
