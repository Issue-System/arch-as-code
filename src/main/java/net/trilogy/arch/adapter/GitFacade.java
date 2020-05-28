package net.trilogy.arch.adapter;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;

import java.io.File;
import java.io.IOException;

public class GitFacade {
    public Git open(File dir) throws IOException {
        return Git.open(dir);
    }

    public Git wrap(Repository repo) {
        return Git.wrap(repo);
    }

}
