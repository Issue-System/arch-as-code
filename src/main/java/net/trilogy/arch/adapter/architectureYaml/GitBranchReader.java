package net.trilogy.arch.adapter.architectureYaml;

import net.trilogy.arch.domain.ArchitectureDataStructure;
import net.trilogy.arch.facade.GitFacade;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.TreeWalk;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class GitBranchReader {
    private final GitFacade gitFacade;
    private final ArchitectureDataStructureObjectMapper objMapper;

    public GitBranchReader(GitFacade gitFacade, ArchitectureDataStructureObjectMapper objMapper) {
        this.gitFacade = gitFacade;
        this.objMapper = objMapper;
    }

    public ArchitectureDataStructure load(String branchName, Path architectureYamlFilePath)
            throws IOException, GitAPIException, BranchNotFoundException {

        var git = gitFacade.openParentRepo(architectureYamlFilePath.toFile());
        final ObjectId resolvedBranch = git.getRepository().resolve(branchName);

        if (resolvedBranch == null) {
            throw new BranchNotFoundException();
        }

        final RevCommit lastCommit = git.log().add(resolvedBranch).call().iterator().next();
        final String relativePath = getRelativePath(architectureYamlFilePath, git);
        final String archAsString = getContent(git, lastCommit, relativePath);

        return objMapper.readValue(archAsString);
    }

    private String getRelativePath(Path architectureYamlFilePath, Git git) {
        return architectureYamlFilePath
                .toAbsolutePath()
                .toString()
                .replaceAll(git.getRepository().getDirectory().getParentFile().getAbsolutePath() + "/", "");
    }

    private String getContent(Git git, RevCommit commit, String path) throws IOException {
        try (TreeWalk treeWalk = TreeWalk.forPath(git.getRepository(), path, commit.getTree())) {
            ObjectId blobId = treeWalk.getObjectId(0);
            try (ObjectReader objectReader = git.getRepository().newObjectReader()) {
                ObjectLoader objectLoader = objectReader.open(blobId);
                byte[] bytes = objectLoader.getBytes();
                return new String(bytes, StandardCharsets.UTF_8);
            }
        }
    }

    public class BranchNotFoundException extends Exception {
    }
}
