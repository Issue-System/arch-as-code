package net.trilogy.arch.commands.architectureUpdate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

import static net.trilogy.arch.commands.architectureUpdate.ArchitectureUpdateCommand.ARCHITECTURE_UPDATES_CLIENT_CREDENTIAL_FILE;

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
        // TODO FUTURE: Test IO DI AAC-75
        createCredentials(clientId, projectId, secret);

        logger.info(String.format("Architecture updates initialized under - %s", Helpers.getAuFolder(productDocumentationRoot)));
        return 0;
    }

    private boolean createCredentials(String clientId, String projectId, String secret) {
        Path cred = Helpers.getAuCredentialFolder(productDocumentationRoot).toPath().resolve(ARCHITECTURE_UPDATES_CLIENT_CREDENTIAL_FILE);
        String credentialJsonString = getCredentialJsonString(clientId, projectId, secret);

        try {
            Files.writeString(cred, credentialJsonString);
            return true;
        } catch (IOException e) {
            logger.error(String.format("Unable to create %s", cred));
            return false;
        }
    }

    private String getCredentialJsonString(String clientId, String projectId, String secret) {
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
        File auFolder = Helpers.getAuFolder(productDocumentationRoot);
        boolean succeeded = auFolder.mkdir();
        if (!succeeded) {
            logger.error(String.format("Unable to create %s", auFolder.getAbsolutePath()));
            return false;
        }
        return true;
    }

    private boolean makeCredentialsFolder() {
        File auCredentialFolder = Helpers.getAuCredentialFolder(productDocumentationRoot);
        boolean credSucceeded = auCredentialFolder.mkdirs();
        if (!credSucceeded) {
            logger.error(String.format("Unable to create %s", auCredentialFolder.getAbsolutePath()));
            return false;
        }
        return true;
    }
}
