package net.trilogy.arch.domain;


import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
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
        var actual = new GitBranchReader(new FilesFacade(), new GitFacade(), new ArchitectureDataStructureObjectMapper())
            .load("master", archPath);
        var expected = new ArchitectureDataStructureObjectMapper().readValue(architectureAsString);

        collector.checkThat(actual, equalTo(expected));
    }

    @Test
    public void shouldNotChangeBranch() throws Exception {
        new GitBranchReader(new FilesFacade(), new GitFacade(), new ArchitectureDataStructureObjectMapper())
            .load("master", archPath);
        
        collector.checkThat(isBranch("not-master"), is(true));
    }

    @Test
    public void shouldPreserveWorkingDirFiles() throws Exception {
        var otherFile = archPath.getParent().resolve("other-file.txt");
        Files.writeString(archPath, "MODIFIED FILE");
        Files.writeString(otherFile, "NEW FILE");
        var beforeFiles = FileUtils.listFilesAndDirs(
                repoDir, 
                FileFilterUtils.trueFileFilter(), 
                FileFilterUtils.notFileFilter(FileFilterUtils.nameFileFilter(".git")));

        new GitBranchReader(new FilesFacade(), new GitFacade(), new ArchitectureDataStructureObjectMapper()).load("master", archPath);

        var afterFiles = FileUtils.listFilesAndDirs(
                repoDir, 
                FileFilterUtils.trueFileFilter(), 
                FileFilterUtils.notFileFilter(FileFilterUtils.nameFileFilter(".git")));

        collector.checkThat(beforeFiles, equalTo(afterFiles));
        collector.checkThat(Files.readString(archPath), equalTo("MODIFIED FILE"));
        collector.checkThat(Files.readString(otherFile), equalTo("NEW FILE"));
    }

    @Test
    public void shouldNotChangeBranchIfException() throws Exception {
        var badMapper = mock(ArchitectureDataStructureObjectMapper.class);
        when(badMapper.readValue(any())).thenThrow(new RuntimeException("Boo!"));

        try{
            new GitBranchReader(new FilesFacade(), new GitFacade(), badMapper).load("master", archPath);
            fail("Exception not thrown.");
        } catch(RuntimeException ignored) {
            collector.checkThat(isBranch("not-master"), is(true));
        }
    }

    @Test
    public void shouldPreserveWorkingDirFilesIfException() throws Exception {
        var badMapper = mock(ArchitectureDataStructureObjectMapper.class);
        when(badMapper.readValue(any())).thenThrow(new RuntimeException("Boo!"));

        var otherFile = archPath.getParent().resolve("other-file.txt");
        Files.writeString(archPath, "MODIFIED FILE");
        Files.writeString(otherFile, "NEW FILE");
        var beforeFiles = FileUtils.listFilesAndDirs(
                repoDir, 
                FileFilterUtils.trueFileFilter(), 
                FileFilterUtils.notFileFilter(FileFilterUtils.nameFileFilter(".git")));

        try {
            new GitBranchReader(new FilesFacade(), new GitFacade(), badMapper).load("master", archPath);
            fail("Exception not thrown.");
        } catch(RuntimeException ignored) {
            var afterFiles = FileUtils.listFilesAndDirs(
                    repoDir, 
                    FileFilterUtils.trueFileFilter(), 
                    FileFilterUtils.notFileFilter(FileFilterUtils.nameFileFilter(".git")));

            collector.checkThat(beforeFiles, equalTo(afterFiles));
            collector.checkThat(Files.readString(archPath), equalTo("MODIFIED FILE"));
            collector.checkThat(Files.readString(otherFile), equalTo("NEW FILE"));
        }
    }
    
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
