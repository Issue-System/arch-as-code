package net.trilogy.arch.transformation.enhancer;

import com.structurizr.Workspace;
import net.trilogy.arch.domain.ArchitectureDataStructure;
import org.junit.Test;

import java.io.File;

import static net.trilogy.arch.TestHelper.TEST_PRODUCT_DOCUMENTATION_ROOT_PATH;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DocumentationEnhancerTest {

    @Test
    public void should_have_two_secions_corresponding_to_two_markdown_files() {
        File root = mock(File.class);
        Workspace workspace = new Workspace("Foo", "Bazz");
        ArchitectureDataStructure dataStructure = mock(ArchitectureDataStructure.class);

        when(root.getAbsolutePath()).thenReturn(getClass().getResource(TEST_PRODUCT_DOCUMENTATION_ROOT_PATH).getPath());
        when(dataStructure.getName()).thenReturn("testspaces");

        new DocumentationEnhancer(root).enhance(workspace, dataStructure);

        assertThat(workspace.getDocumentation().getSections(), hasSize(2));
    }
}
