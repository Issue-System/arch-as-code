package net.trilogy.arch.domain;


import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import net.trilogy.arch.TestHelper;
import net.trilogy.arch.adapter.architectureYaml.ArchitectureDataStructureObjectMapper;
import net.trilogy.arch.adapter.architectureYaml.GitBranchReader;
import net.trilogy.arch.facade.FilesFacade;
import net.trilogy.arch.facade.GitFacade;
import static org.hamcrest.Matchers.*;

public class GitBranchReaderTest {
    @Rule
    public final ErrorCollector collector = new ErrorCollector();

    private File repoDir;
    private File rootDir;
    private Path archPath;
    private String architectureAsString;

    @After
    public void tearDown() throws Exception {
        FileUtils.forceDelete(repoDir);
    }

    @Before
    public void setUp() throws Exception {
        repoDir = Files.createTempDirectory("aac").toFile();
        var git = Git.init().setDirectory(repoDir).call();

        rootDir = Files.createDirectory(repoDir.toPath().resolve("root")).toFile();
        archPath = rootDir.toPath().resolve("product-architecture.yml");

        architectureAsString = TestHelper.loadResource(getClass(), TestHelper.MANIFEST_PATH_TO_TEST_GENERALLY);
        Files.writeString(archPath, architectureAsString);
        git.add().addFilepattern("root/product-architecture.yml").call();
        git.commit().setMessage("commit architecture to master").call();

        collector.checkThat(
            "PRECONDITION CHECK: Architecture must exist in master branch.",
            Files.exists(archPath),
            is(true)
        );

        git.checkout().setCreateBranch(true).setName("not-master").call();
        Files.delete(archPath);
        git.add().setUpdate(true).addFilepattern("root/product-architecture.yml").call();
        git.commit().setMessage("commit deleting architecture in other branch").call();

        collector.checkThat(
            "PRECONDITION CHECK: Architecture must not exist in current branch.",
            Files.exists(archPath),
            is(false)
        );
    }

    @Test
    public void shouldLoadMasterBranchArchitecture() throws Exception {
        var actual = new GitBranchReader(new FilesFacade(), new GitFacade()).load("master", archPath);
        var expected = new ArchitectureDataStructureObjectMapper().readValue(architectureAsString);

        collector.checkThat(actual, equalTo(expected));
    }

    @Test
    public void shouldNotChangeBranch()
            throws RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException,
            CheckoutConflictException, IOException, GitAPIException {
        new GitBranchReader(new FilesFacade(), new GitFacade()).load("master", archPath);
        
        collector.checkThat(isBranch("not-master"), is(true));
    }

    // check file contents
    // check stash contents
    
    // check branch if exception thrown
    // check file contents if exception thrown
    // check stash contents if exception thrown
    
    @Ignore("TODO")
    @Test
    public void shouldHandleIfBranchInvalid() {
        fail("WIP");
    }

    private boolean isBranch(String wantedBranch) throws IOException {
        return new GitFacade()
                .openParentRepo(archPath.toFile().getParentFile())
                .getRepository()
                .getBranch()
                .equals(wantedBranch);
    }

}
