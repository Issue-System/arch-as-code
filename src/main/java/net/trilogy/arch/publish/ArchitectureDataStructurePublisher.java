package net.trilogy.arch.publish;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.structurizr.Workspace;
import com.structurizr.api.StructurizrClientException;
import com.structurizr.view.ViewSet;
import net.trilogy.arch.adapter.architectureYaml.ArchitectureDataStructureObjectMapper;
import net.trilogy.arch.adapter.structurizr.StructurizrAdapter;
import net.trilogy.arch.domain.ArchitectureDataStructure;
import net.trilogy.arch.facade.FilesFacade;
import net.trilogy.arch.transformation.ArchitectureDataStructureTransformer;
import net.trilogy.arch.transformation.TransformerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import static net.trilogy.arch.adapter.architectureYaml.ArchitectureDataStructureWriter.STRUCTURIZR_VIEWS_FILE_NAME;

public class ArchitectureDataStructurePublisher {
    private final File productArchitectureDirectory;
    private final FilesFacade filesFacade;
    private final ArchitectureDataStructureTransformer dataStructureTransformer;
    private final StructurizrAdapter structurizrAdapter;
    private final String manifestFileName;

    public ArchitectureDataStructurePublisher(FilesFacade filesFacade,
                                              File productArchitectureDirectory,
                                              String manifestFileName) {
        this.filesFacade = filesFacade;
        this.productArchitectureDirectory = productArchitectureDirectory;
        this.manifestFileName = manifestFileName;
        this.dataStructureTransformer = TransformerFactory.create(productArchitectureDirectory);
        this.structurizrAdapter = new StructurizrAdapter();
    }

    public ArchitectureDataStructurePublisher(StructurizrAdapter structurizrAdapter,
                                              FilesFacade filesFacade,
                                              File productArchitectureDirectory,
                                              String manifestFileName) {
        this.filesFacade = filesFacade;
        this.productArchitectureDirectory = productArchitectureDirectory;
        this.manifestFileName = manifestFileName;
        this.dataStructureTransformer = TransformerFactory.create(productArchitectureDirectory);
        this.structurizrAdapter = structurizrAdapter;
    }

    // TODO [TESTING] [OVERHAUL] [OPTIONAL]: Make testing not require real connection to Structurizr.
    public void publish() throws StructurizrClientException, IOException {
        Workspace workspace = getWorkspace(productArchitectureDirectory, manifestFileName);
//        File file = new File(productArchitectureDirectory + File.separator + "workspace.json");
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.writeValue(file, workspace);
        structurizrAdapter.publish(workspace);
    }

    public Workspace getWorkspace(File productArchitectureDirectory, String manifestFileName) throws IOException {
        ArchitectureDataStructure dataStructure = loadProductArchitecture(productArchitectureDirectory, manifestFileName);

        Workspace workspace = dataStructureTransformer.toWorkSpace(dataStructure);

        loadAndSetViews(productArchitectureDirectory, workspace);

        return workspace;
    }

    private void loadAndSetViews(File productArchitectureDirectory, Workspace workspace) throws IOException {
        ViewSet viewSet = loadStructurizrViews(productArchitectureDirectory);
        try {
            Method setViews = Workspace.class.getDeclaredMethod("setViews", ViewSet.class);
            setViews.setAccessible(true);
            setViews.invoke(workspace, viewSet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ViewSet loadStructurizrViews(File productArchitectureDirectory) throws IOException {
        File manifestFile = new File(productArchitectureDirectory + File.separator + STRUCTURIZR_VIEWS_FILE_NAME);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(manifestFile, ViewSet.class);
    }

    public ArchitectureDataStructure loadProductArchitecture(File productArchitectureDirectory, String manifestFileName) throws IOException {
        File manifestFile = new File(productArchitectureDirectory + File.separator + manifestFileName);
        String archAsString = this.filesFacade.readString(manifestFile.toPath());

        return new ArchitectureDataStructureObjectMapper().readValue(archAsString);
    }
}
