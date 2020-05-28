package net.trilogy.arch.adapter;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;

public class GitFacade {
    public Git openParentRepo(File dir) throws IOException {
        return Git.wrap(getRepository(dir));
    }

    private static Repository getRepository(File rootDir) throws IOException {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        return builder
                .readEnvironment()
                .findGitDir(rootDir)
                .build();
    }

}
