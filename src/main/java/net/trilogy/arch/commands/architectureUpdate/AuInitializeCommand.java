package net.trilogy.arch.commands.architectureUpdate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.Callable;

import static net.trilogy.arch.adapter.in.google.GoogleDocsAuthorizedApiFactory.GOOGLE_DOCS_API_CLIENT_CREDENTIALS_FILE_NAME;
import static net.trilogy.arch.adapter.in.google.GoogleDocsAuthorizedApiFactory.GOOGLE_DOCS_API_CREDENTIALS_FOLDER_PATH;

@Command(name = "initialize", aliases = "init", description = "Initialize the architecture updates work space.")
public class AuInitializeCommand implements Callable<Integer> {
    private static final Log logger = LogFactory.getLog(AuInitializeCommand.class);

    @CommandLine.Option(names = {"-c", "--client-id"}, description = "Google API client id", required = true)
    private String clientId;

    @CommandLine.Option(names = {"-p", "--project-id"}, description = "Google API project id", required = true)
    private String projectId;

    @CommandLine.Option(names = {"-s", "--secret"}, description = "Google API secret", required = true)
    private String secret;


    @Parameters(index = "0", description = "Product documentation root directory")
    private File productDocumentationRoot;

    private final String authUri = "https://accounts.google.com/o/oauth2/auth";
    private final String tokenUri = "https://oauth2.googleapis.com/token";
    private final String authProviderCertUrl = "https://www.googleapis.com/oauth2/v1/certs";
    private final String redirectUrn = "urn:ietf:wg:oauth:2.0:oob";
    private final String redirectUri = "http://localhost";

    @Override
    public Integer call() {
        if (!makeAuFolder()) return 1;
        if (!makeCredentialsFolder()) return 1;

        // TODO FUTURE: [Dependencies: AAC-75] Test case where creating file fails
        createClientCredentialsFile(clientId, projectId, secret);

        logger.info(String.format("Architecture updates initialized under - %s", productDocumentationRoot.toPath().resolve(ArchitectureUpdateCommand.ARCHITECTURE_UPDATES_ROOT_FOLDER).toFile()));
        return 0;
    }

    private boolean createClientCredentialsFile(String clientId, String projectId, String secret) {
        File file = productDocumentationRoot.toPath().resolve(GOOGLE_DOCS_API_CREDENTIALS_FOLDER_PATH).resolve(GOOGLE_DOCS_API_CLIENT_CREDENTIALS_FILE_NAME).toFile();
        String credentialJsonString = buildCredentialJsonString(clientId, projectId, secret);
        try {
            Files.writeString(file.toPath(), credentialJsonString);
            return true;
        } catch (IOException e) {
            logger.error(String.format("Unable to create %s", file.getAbsolutePath()));
            return false;
        }
    }

    private String buildCredentialJsonString(String clientId, String projectId, String secret) {
        return "{\n" +
                "  \"installed\": {\n" +
                "    \"client_id\": \"" + clientId.strip() + "\",\n" +
                "    \"project_id\": \"" + projectId.strip() + "\",\n" +
                "    \"auth_uri\": \"" + authUri + "\",\n" +
                "    \"token_uri\": \"" + tokenUri + "\",\n" +
                "    \"auth_provider_x509_cert_url\": \"" + authProviderCertUrl + "\",\n" +
                "    \"client_secret\": \"" + secret.strip() + "\",\n" +
                "    \"redirect_uris\": [\n" +
                "      \"" + redirectUrn + "\",\n" +
                "      \"" + redirectUri + "\"\n" +
                "    ]\n" +
                "  }\n" +
                "}";
    }

    private boolean makeAuFolder() {
        File auFolder = productDocumentationRoot.toPath().resolve(ArchitectureUpdateCommand.ARCHITECTURE_UPDATES_ROOT_FOLDER).toFile();
        boolean succeeded = auFolder.mkdir();
        if (!succeeded) {
            logger.error(String.format("Unable to create %s", auFolder.getAbsolutePath()));
            return false;
        }
        return true;
    }

    private boolean makeCredentialsFolder() {
        File auCredentialFolder = productDocumentationRoot.toPath()
                .resolve(GOOGLE_DOCS_API_CREDENTIALS_FOLDER_PATH).toFile();

        boolean credSucceeded = auCredentialFolder.mkdirs();
        if (!credSucceeded) {
            logger.error(String.format("Unable to create %s", auCredentialFolder.getAbsolutePath()));
            return false;
        }
        return true;
    }
}
