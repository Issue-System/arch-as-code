package net.trilogy.arch;

import net.trilogy.arch.adapter.in.google.GoogleDocsAuthorizedApiFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static net.trilogy.arch.Bootstrap.GOOGLE_DOCS_API_CLIENT_CREDENTIALS_PATH;
import static net.trilogy.arch.Bootstrap.GOOGLE_DOCS_API_USER_CREDENTIALS_DIR_PATH;

public abstract class TestHelper {
    public static Long TEST_WORKSPACE_ID = 49344L;

    public static String MANIFEST_PATH_TO_TEST_GENERALLY = "/architecture/products/testspaces/data-structure.yml";
    public static String MANIFEST_PATH_TO_TEST_DECISIONS = "/architecture/products/testspaces/data-structure.yml";
    public static String MANIFEST_PATH_TO_TEST_MODEL_PEOPLE = "/architecture/products/testspaces/data-structure.yml";
    public static String MANIFEST_PATH_TO_TEST_MODEL_SYSTEMS = "/architecture/products/testspaces/data-structure.yml";
    public static String MANIFEST_PATH_TO_TEST_MODEL_CONTAINERS = "/architecture/products/testspaces/data-structure.yml";
    public static String MANIFEST_PATH_TO_TEST_MODEL_COMPONENTS = "/architecture/products/testspaces/data-structure.yml";
    public static String MANIFEST_PATH_TO_TEST_MODEL_DEPLOYMENT_NODES = "/view/bigBank/data-structure.yml";
    public static String MANIFEST_PATH_TO_TEST_VIEWS = "/view/bigBank/data-structure.yml";
    public static String MANIFEST_PATH_TO_TEST_METADATA = "/architecture/products/testspaces/data-structure.yml";

    public static String JSON_STRUCTURIZR_BIG_BANK = "/structurizr/BigBank.json";
    public static String JSON_STRUCTURIZR_THINK3_SOCOCO = "/structurizr/Think3-Sococo.c4model.json";

    public static String ROOT_PATH_TO_TEST_PRODUCT_DOCUMENTATION = "/architecture/products/testspaces/";
    public static String ROOT_PATH_TO_TEST_VALIDATION = "/validation/";
    public static String ROOT_PATH_TO_TEST_VIEWS = "/view/bigBank/";

    public static Integer execute(String... args) throws GeneralSecurityException, IOException {
        var googleDocsApiFactory = new GoogleDocsAuthorizedApiFactory(GOOGLE_DOCS_API_CLIENT_CREDENTIALS_PATH, GOOGLE_DOCS_API_USER_CREDENTIALS_DIR_PATH);
        return new Bootstrap(googleDocsApiFactory).execute(args);
    }

    public static Integer execute(Bootstrap bootstrap, String command) {
        return bootstrap.execute(command.split(" "));
    }
}
