package net.nahknarmi.arch.transformation;

import com.structurizr.Workspace;
import com.structurizr.documentation.Decision;
import com.structurizr.documentation.DecisionStatus;
import net.nahknarmi.arch.model.ArchitectureDataStructure;
import net.nahknarmi.arch.model.ImportantTechnicalDecision;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class ArchitectureDataStructureTransformerTest {
    private static final String PRODUCT_NAME = "devspaces";
    private static final String PRODUCT_DESCRIPTION = "DevSpaces is a tool";

    @Test
    public void should_transform_architecture_yaml_to_structurizr_workspace() throws IOException {
        ArchitectureDataStructure dataStructure = new ArchitectureDataStructure();
        dataStructure.setName(PRODUCT_NAME);
        dataStructure.setDescription(PRODUCT_DESCRIPTION);
        dataStructure.setId(1L);

        ArchitectureDataStructureTransformer transformer = new ArchitectureDataStructureTransformer();
        Workspace workspace = transformer.toWorkSpace(dataStructure);

        assertNotNull(workspace);
        assertThat(workspace.getId(), equalTo(1L));
        assertThat(workspace.getName(), equalTo(PRODUCT_NAME));
        assertThat(workspace.getDescription(), equalTo(PRODUCT_DESCRIPTION));
        assertThat(workspace.getDocumentation().getSections().size(), equalTo(2));
    }

    @Test
    public void should_tranform_accept_decision_status() throws IOException {
        ArchitectureDataStructure dataStructure = new ArchitectureDataStructure();
        dataStructure.setName(PRODUCT_NAME);
        dataStructure.setDescription(PRODUCT_DESCRIPTION);
        dataStructure.setId(1L);

        List<ImportantTechnicalDecision> itds = new ArrayList<ImportantTechnicalDecision>();
        itds.add(new ImportantTechnicalDecision("1", new Date(), "title", "Accepted", "content"));
        dataStructure.setDecisions(itds);

        ArchitectureDataStructureTransformer transformer = new ArchitectureDataStructureTransformer();
        Workspace workspace = transformer.toWorkSpace(dataStructure);

        ArrayList<Decision> decisions = new ArrayList<>(workspace.getDocumentation().getDecisions());
        DecisionStatus result = decisions.get(0).getStatus();

        assertThat(result, equalTo(DecisionStatus.Accepted));
    }

    @Test
    public void should_tranform_superseded_decision_status() throws IOException {
        ArchitectureDataStructure dataStructure = new ArchitectureDataStructure();
        dataStructure.setName(PRODUCT_NAME);
        dataStructure.setDescription(PRODUCT_DESCRIPTION);
        dataStructure.setId(1L);

        List<ImportantTechnicalDecision> itds = new ArrayList<ImportantTechnicalDecision>();
        itds.add(new ImportantTechnicalDecision("1", new Date(), "title", "Superseded", "content"));
        dataStructure.setDecisions(itds);

        ArchitectureDataStructureTransformer transformer = new ArchitectureDataStructureTransformer();
        Workspace workspace = transformer.toWorkSpace(dataStructure);

        ArrayList<Decision> decisions = new ArrayList<>(workspace.getDocumentation().getDecisions());
        DecisionStatus result = decisions.get(0).getStatus();

        assertThat(result, equalTo(DecisionStatus.Superseded));
    }

    @Test
    public void should_tranform_deprecated_decision_status() throws IOException {
        ArchitectureDataStructure dataStructure = new ArchitectureDataStructure();
        dataStructure.setName(PRODUCT_NAME);
        dataStructure.setDescription(PRODUCT_DESCRIPTION);
        dataStructure.setId(1L);

        List<ImportantTechnicalDecision> itds = new ArrayList<ImportantTechnicalDecision>();
        itds.add(new ImportantTechnicalDecision("1", new Date(), "title", "Deprecated", "content"));
        dataStructure.setDecisions(itds);

        ArchitectureDataStructureTransformer transformer = new ArchitectureDataStructureTransformer();
        Workspace workspace = transformer.toWorkSpace(dataStructure);

        ArrayList<Decision> decisions = new ArrayList<>(workspace.getDocumentation().getDecisions());
        DecisionStatus result = decisions.get(0).getStatus();

        assertThat(result, equalTo(DecisionStatus.Deprecated));
    }

    @Test
    public void should_tranform_rejected_decision_status() throws IOException {
        ArchitectureDataStructure dataStructure = new ArchitectureDataStructure();
        dataStructure.setName(PRODUCT_NAME);
        dataStructure.setDescription(PRODUCT_DESCRIPTION);
        dataStructure.setId(1L);

        List<ImportantTechnicalDecision> itds = new ArrayList<ImportantTechnicalDecision>();
        itds.add(new ImportantTechnicalDecision("1", new Date(), "title", "Rejected", "content"));
        dataStructure.setDecisions(itds);

        ArchitectureDataStructureTransformer transformer = new ArchitectureDataStructureTransformer();
        Workspace workspace = transformer.toWorkSpace(dataStructure);

        ArrayList<Decision> decisions = new ArrayList<>(workspace.getDocumentation().getDecisions());
        DecisionStatus result = decisions.get(0).getStatus();

        assertThat(result, equalTo(DecisionStatus.Rejected));
    }

    @Test
    public void should_tranform_proposed_decision_status() throws IOException {
        ArchitectureDataStructure dataStructure = new ArchitectureDataStructure();
        dataStructure.setName(PRODUCT_NAME);
        dataStructure.setDescription(PRODUCT_DESCRIPTION);
        dataStructure.setId(1L);

        List<ImportantTechnicalDecision> itds = new ArrayList<ImportantTechnicalDecision>();
        itds.add(new ImportantTechnicalDecision("1", new Date(), "title", "Proposed", "content"));
        dataStructure.setDecisions(itds);

        ArchitectureDataStructureTransformer transformer = new ArchitectureDataStructureTransformer();
        Workspace workspace = transformer.toWorkSpace(dataStructure);

        ArrayList<Decision> decisions = new ArrayList<>(workspace.getDocumentation().getDecisions());
        DecisionStatus result = decisions.get(0).getStatus();

        assertThat(result, equalTo(DecisionStatus.Proposed));
    }

    @Test
    public void should_tranform_decision_status_to_default() throws IOException {
        ArchitectureDataStructure dataStructure = new ArchitectureDataStructure();
        dataStructure.setName(PRODUCT_NAME);
        dataStructure.setDescription(PRODUCT_DESCRIPTION);
        dataStructure.setId(1L);

        List<ImportantTechnicalDecision> itds = new ArrayList<ImportantTechnicalDecision>();
        itds.add(new ImportantTechnicalDecision("1", new Date(), "title", "invalid status defaults to proposed", "content"));
        dataStructure.setDecisions(itds);

        ArchitectureDataStructureTransformer transformer = new ArchitectureDataStructureTransformer();
        Workspace workspace = transformer.toWorkSpace(dataStructure);

        ArrayList<Decision> decisions = new ArrayList<>(workspace.getDocumentation().getDecisions());
        DecisionStatus result = decisions.get(0).getStatus();

        assertThat(result, equalTo(DecisionStatus.Proposed));
    }

    //handle id being absent, name, description.

}
