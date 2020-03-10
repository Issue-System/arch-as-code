package net.trilogy.arch.transformation.enhancer;

import com.structurizr.Workspace;
import net.trilogy.arch.domain.ArchitectureDataStructure;
import net.trilogy.arch.domain.ImportantTechnicalDecision;
import org.junit.Test;

import java.util.Date;

import static com.google.common.collect.ImmutableList.of;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DecisionEnhancerTest {

    @Test
    public void no_decisions_when_empty_decision_list() {
        Workspace workspace = new Workspace("foo", "bar");
        ArchitectureDataStructure dataStructure = mock(ArchitectureDataStructure.class);

        when(dataStructure.getDecisions()).thenReturn(emptyList());

        new DecisionEnhancer().enhance(workspace, dataStructure);

        assertThat(workspace.getDocumentation().getDecisions(), hasSize(0));
    }

    @Test
    public void one_decision_when_single_decision_list() {
        Workspace workspace = new Workspace("foo", "bar");
        ArchitectureDataStructure dataStructure = mock(ArchitectureDataStructure.class);

        when(dataStructure.getDecisions()).thenReturn(of(new ImportantTechnicalDecision("1", new Date(), "title", "", "##Some content")));

        new DecisionEnhancer().enhance(workspace, dataStructure);

        assertThat(workspace.getDocumentation().getDecisions(), hasSize(1));
    }

    @Test
    public void two_decision_when_two_decision_list() {
        Workspace workspace = new Workspace("foo", "bar");
        ArchitectureDataStructure dataStructure = mock(ArchitectureDataStructure.class);

        when(dataStructure.getDecisions())
                .thenReturn(of(
                        new ImportantTechnicalDecision("1", new Date(), "title", "", "##Some content"),
                        new ImportantTechnicalDecision("2", new Date(), "title", "", "##Some content"))
                );

        new DecisionEnhancer().enhance(workspace, dataStructure);

        assertThat(workspace.getDocumentation().getDecisions(), hasSize(2));
    }
}
