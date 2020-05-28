package net.trilogy.arch.facade;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;

public class GitFacade {
    public Git openParentRepo(File dir) throws IOException {
        return Git.wrap(
            new FileRepositoryBuilder()
                .findGitDir(toAbsolute(dir))
                .build()
        );
    }

    private static File toAbsolute(File dir){
        return dir.toPath().toAbsolutePath().toFile();
    }
}
