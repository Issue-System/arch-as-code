package net.trilogy.arch.adapter.architectureYaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;

import net.trilogy.arch.domain.ArchitectureDataStructure;
import net.trilogy.arch.facade.FilesFacade;
import net.trilogy.arch.facade.GitFacade;

public class GitBranchReader {
    private final FilesFacade filesFacade;
    private final GitFacade gitFacade;

    public GitBranchReader(FilesFacade filesFacade, GitFacade gitFacade) {
        this.filesFacade = filesFacade;
        this.gitFacade = gitFacade;
    }

    public ArchitectureDataStructure load(String branchName, Path architectureYamlFilePath)
            throws IOException, RefAlreadyExistsException, RefNotFoundException,
            InvalidRefNameException, CheckoutConflictException, GitAPIException {

        var repo = gitFacade.openParentRepo(architectureYamlFilePath.toFile());
        var originalBranch = repo.getRepository().getBranch();

        repo.checkout().setName(branchName).call();

        var arch = new ArchitectureDataStructureObjectMapper()
                            .readValue(filesFacade.readString(architectureYamlFilePath));

        repo.checkout().setName(originalBranch).call();

        return arch;
    }
}
