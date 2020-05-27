package net.trilogy.arch.adapter;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;

public class GitFacade {
	public Git open(File dir) throws IOException {
        return Git.open(dir);
    }
}
