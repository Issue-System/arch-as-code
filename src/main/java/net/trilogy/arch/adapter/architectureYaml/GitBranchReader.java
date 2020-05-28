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
    private final ArchitectureDataStructureObjectMapper objMapper;

    public GitBranchReader(FilesFacade filesFacade, GitFacade gitFacade, ArchitectureDataStructureObjectMapper objMapper) {
        this.filesFacade = filesFacade;
        this.gitFacade = gitFacade;
        this.objMapper = objMapper;
    }

    public ArchitectureDataStructure load(String branchName, Path architectureYamlFilePath)
            throws IOException, RefAlreadyExistsException, RefNotFoundException,
            InvalidRefNameException, CheckoutConflictException, GitAPIException {

        var repo = gitFacade.openParentRepo(architectureYamlFilePath.toFile());
        var originalBranch = repo.getRepository().getBranch();

        filesFacade.writeString(architectureYamlFilePath.getParent().resolve("empty.txt"), "");
        repo.stashCreate().setIncludeUntracked(true).call();

        repo.checkout().setName(branchName).call();

        var arch = objMapper.readValue(filesFacade.readString(architectureYamlFilePath));

        repo.checkout().setName(originalBranch).call();

        repo.stashApply().call();

        return arch;
    }
}
