package net.trilogy.arch.transformation.enhancer;

import com.structurizr.Workspace;
import net.trilogy.arch.domain.ArchitectureDataStructure;
import org.junit.Test;

import java.io.File;
import java.util.Map;
import java.util.stream.Collectors;

import static net.trilogy.arch.TestHelper.ROOT_PATH_TO_TEST_PRODUCT_DOCUMENTATION;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DocumentationEnhancerTest {

    @Test
    public void should_have_two_sections_corresponding_to_two_markdown_files() {
        File root = mock(File.class);
        Workspace workspace = new Workspace("Foo", "Bazz");
        ArchitectureDataStructure dataStructure = mock(ArchitectureDataStructure.class);

        when(root.getAbsolutePath()).thenReturn(getClass().getResource(ROOT_PATH_TO_TEST_PRODUCT_DOCUMENTATION).getPath());
        when(dataStructure.getName()).thenReturn("testspaces");

        new DocumentationEnhancer(root).enhance(workspace, dataStructure);

        assertThat(workspace.getDocumentation().getSections(), hasSize(4));
    }

    @Test
    public void shouldPreserveOrderOfDocumentation() {
        Workspace workspace = new Workspace("Foo", "Bazz");

        final String file = getClass().getResource(ROOT_PATH_TO_TEST_PRODUCT_DOCUMENTATION).getFile();
        new DocumentationEnhancer(new File(file)).enhance(workspace, new ArchitectureDataStructure());

        assertThat(workspace.getDocumentation().getSections().size(), equalTo(4));

        final var expected = Map.of(
                1, "context-diagram",
                2, "functional-overview",
                3, "Ascii-docs",
                4, "no_order"
        );
        final var actual = workspace.getDocumentation().getSections().stream()
                .collect(Collectors.toMap(d -> d.getOrder(), d -> d.getTitle()));

        assertThat(actual, equalTo(expected));
    }
}
