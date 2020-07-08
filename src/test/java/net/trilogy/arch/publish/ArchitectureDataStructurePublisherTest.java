package net.trilogy.arch.publish;

import com.structurizr.Workspace;
import net.trilogy.arch.TestHelper;
import net.trilogy.arch.adapter.architectureYaml.ArchitectureDataStructureReader;
import net.trilogy.arch.adapter.structurizr.StructurizrAdapter;
import net.trilogy.arch.domain.ArchitectureDataStructure;
import net.trilogy.arch.facade.FilesFacade;
import net.trilogy.arch.transformation.ArchitectureDataStructureTransformer;
import net.trilogy.arch.transformation.TransformerFactory;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ArchitectureDataStructurePublisherTest {

    @Test
    public void given_file_that_exists_should_publish_workspace() throws Exception {
        File productArchitectureDirectory = mock(File.class);
        ArchitectureDataStructureReader importer = mock(ArchitectureDataStructureReader.class);
        ArchitectureDataStructureTransformer transformer = mock(ArchitectureDataStructureTransformer.class);
        StructurizrAdapter adapter = mock(StructurizrAdapter.class);
        FilesFacade filesFacade = mock(FilesFacade.class);

        when(importer.load(any())).thenReturn(new ArchitectureDataStructure());
        when(transformer.toWorkSpace(any())).thenReturn(new Workspace("any", "any"));

        //when
        when(productArchitectureDirectory.exists()).thenReturn(true);
        new ArchitectureDataStructurePublisher(productArchitectureDirectory, importer, filesFacade, transformer, adapter).publish();

        //then
        verify(importer, times(1)).load(any(File.class));
        verify(transformer, times(1)).toWorkSpace(any(ArchitectureDataStructure.class));
        verify(adapter, times(1)).publish(any(Workspace.class));
    }

    @Test
    public void shouldLoadProductArchitecture() throws Exception {
        File productArchitectureDir = new File(getClass().getResource(TestHelper.ROOT_PATH_TO_TEST_GENERALLY).getPath());
        ArchitectureDataStructurePublisher publisher = new ArchitectureDataStructurePublisher(productArchitectureDir,
                new ArchitectureDataStructureReader(new FilesFacade()),
                new FilesFacade(),
                TransformerFactory.create(productArchitectureDir),
                new StructurizrAdapter());

        ArchitectureDataStructure dataStructure = publisher.loadProductArchitecture(productArchitectureDir, "product-architecture.yml");

        assertThat(dataStructure.getName(), equalTo("TestSpaces"));
    }
}
