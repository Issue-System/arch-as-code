package net.trilogy.arch.commands.architectureUpdate;

import java.io.File;

class Helpers {
    static File getAuFolder(File productDocumentationRoot) {
        return productDocumentationRoot.toPath().resolve(ArchitectureUpdateCommand.ARCHITECTURE_UPDATES_ROOT_FOLDER).toFile();
    }

    static File getAuCredentialFolder(File productDocumentationRoot) {
        return productDocumentationRoot.toPath().resolve(ArchitectureUpdateCommand.ARCHITECTURE_UPDATES_CREDENTIAL_FOLDER).toFile();
    }
}
