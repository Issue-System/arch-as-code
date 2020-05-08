package net.trilogy.arch.commands.architectureUpdate;

import net.trilogy.arch.adapter.FilesFacade;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import static net.trilogy.arch.adapter.Jira.JiraApiFactory.JIRA_API_SETTINGS_FILE_PATH;
import static net.trilogy.arch.adapter.in.google.GoogleDocsAuthorizedApiFactory.GOOGLE_DOCS_API_CLIENT_CREDENTIALS_FILE_NAME;
import static net.trilogy.arch.adapter.in.google.GoogleDocsAuthorizedApiFactory.GOOGLE_DOCS_API_CREDENTIALS_FOLDER_PATH;

@Command(name = "initialize", aliases = "init", mixinStandardHelpOptions = true, description = "Initialize the architecture updates work space within a single product's existing workspace. Sets up Google API credentials to import P1 documents.")
public class AuInitializeCommand implements Callable<Integer> {
    private static final Log logger = LogFactory.getLog(AuInitializeCommand.class);
    private final FilesFacade filesFacade;

    @CommandLine.Option(names = {"-c", "--client-id"}, description = "Google API client id", required = true)
    private String googleApiClientId;

    @CommandLine.Option(names = {"-p", "--project-id"}, description = "Google API project id", required = true)
    private String googleApiProjectId;

    @CommandLine.Option(names = {"-s", "--secret"}, description = "Google API secret", required = true)
    private String googleApiSecret;

    @Parameters(index = "0", description = "Product workspace directory, containng the product's architecture")
    private File productDocumentationRoot;

    private final String INITIAL_GOOGLE_API_AUTH_URI = "https://accounts.google.com/o/oauth2/auth";
    private final String INITIAL_GOOGLE_API_TOKEN_URI = "https://oauth2.googleapis.com/token";
    private final String INITIAL_GOOGLE_API_AUTH_PROVIDER_CERT_URL = "https://www.googleapis.com/oauth2/v1/certs";
    private final String INITIAL_GOOGLE_API_REDIRECT_URN = "urn:ietf:wg:oauth:2.0:oob";
    private final String INITIAL_GOOGLE_API_REDIRECT_URI = "http://localhost";
    private final String INITIAL_JIRA_BASE_URI = "http://jira.devfactory.com";
    private final String INITIAL_JIRA_LINK_PREFIX = "/browse/";
    private final String INITIAL_JIRA_GET_STORY_ENDPOINT = "/rest/api/2/issue/";
    private final String INITIAL_JIRA_BULK_CREATE_ENDPOINT = "/rest/api/2/issue/bulk";

    public AuInitializeCommand(FilesFacade filesFacade) {
        this.filesFacade = filesFacade;
    }

    @Override
    public Integer call() {
        if (!makeAuFolder()) return 1;
        if (!makeJiraSettingsFile()) return 1;
        if (!makeGoogleApiCredentialsFolder()) return 1;
        if (!createGoogleApiClientCredentialsFile(googleApiClientId, googleApiProjectId, googleApiSecret)) return 1;

        logger.info(String.format("Architecture updates initialized under - %s", productDocumentationRoot.toPath().resolve(AuCommand.ARCHITECTURE_UPDATES_ROOT_FOLDER).toFile()));
        return 0;
    }

    private boolean makeJiraSettingsFile() {
        File file = productDocumentationRoot.toPath().resolve(JIRA_API_SETTINGS_FILE_PATH).toFile();
        if (!file.getParentFile().mkdirs()) return false;
        try {
            filesFacade.writeString(file.toPath(), buildJiraSettingsJsonString());
            return true;
        } catch (IOException e) {
            logger.error(String.format("Unable to create %s", file.getAbsolutePath()));
            return false;
        }
    }

    private boolean createGoogleApiClientCredentialsFile(String clientId, String projectId, String secret) {
        File file = productDocumentationRoot.toPath().resolve(GOOGLE_DOCS_API_CREDENTIALS_FOLDER_PATH).resolve(GOOGLE_DOCS_API_CLIENT_CREDENTIALS_FILE_NAME).toFile();
        String credentialJsonString = buildCredentialJsonString(clientId, projectId, secret);
        try {
            filesFacade.writeString(file.toPath(), credentialJsonString);
            return true;
        } catch (IOException e) {
            logger.error(String.format("Unable to create %s", file.getAbsolutePath()));
            return false;
        }
    }

    private String buildJiraSettingsJsonString() {
        return "{\n" +
                "    \"base_uri\": \"" + INITIAL_JIRA_BASE_URI + "\",\n" +
                "    \"link_prefix\": \"" + INITIAL_JIRA_LINK_PREFIX + "\",\n" +
                "    \"get_story_endpoint\": \"" + INITIAL_JIRA_GET_STORY_ENDPOINT + "\",\n" +
                "    \"bulk_create_endpoint\": \"" + INITIAL_JIRA_BULK_CREATE_ENDPOINT + "\"\n" +
                "}";

    }

    private String buildCredentialJsonString(String clientId, String projectId, String secret) {
        return "{\n" +
                "  \"installed\": {\n" +
                "    \"client_id\": \"" + clientId.strip() + "\",\n" +
                "    \"project_id\": \"" + projectId.strip() + "\",\n" +
                "    \"auth_uri\": \"" + INITIAL_GOOGLE_API_AUTH_URI + "\",\n" +
                "    \"token_uri\": \"" + INITIAL_GOOGLE_API_TOKEN_URI + "\",\n" +
                "    \"auth_provider_x509_cert_url\": \"" + INITIAL_GOOGLE_API_AUTH_PROVIDER_CERT_URL + "\",\n" +
                "    \"client_secret\": \"" + secret.strip() + "\",\n" +
                "    \"redirect_uris\": [\n" +
                "      \"" + INITIAL_GOOGLE_API_REDIRECT_URN + "\",\n" +
                "      \"" + INITIAL_GOOGLE_API_REDIRECT_URI + "\"\n" +
                "    ]\n" +
                "  }\n" +
                "}";
    }

    private boolean makeAuFolder() {
        File auFolder = productDocumentationRoot.toPath().resolve(AuCommand.ARCHITECTURE_UPDATES_ROOT_FOLDER).toFile();
        boolean succeeded = auFolder.mkdir();
        if (!succeeded) {
            logger.error(String.format("Unable to create %s", auFolder.getAbsolutePath()));
            return false;
        }
        return true;
    }

    private boolean makeGoogleApiCredentialsFolder() {
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
