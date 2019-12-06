package net.nahknarmi.arch.adapter;

import com.structurizr.Workspace;
import com.structurizr.documentation.Format;
import com.structurizr.documentation.StructurizrDocumentationTemplate;
import com.structurizr.model.Model;
import com.structurizr.model.Person;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.model.Tags;
import com.structurizr.view.*;

import java.time.LocalDateTime;
import java.util.Optional;

public class ModelBuilder {

    void buildModel(Workspace workspace) {
        workspace.setDescription(String.format("Architecture as Code - %s", LocalDateTime.now()));

        Model model = workspace.getModel();
        SoftwareSystem softwareSystem = assignSoftwareArchitectureModel(model);

        ViewSet views = assignViews(workspace, softwareSystem);
        assignDocumentation(workspace, softwareSystem);
        assignStyles(views);
    }

    private SoftwareSystem assignSoftwareArchitectureModel(Model model) {
        Person user = model.addPerson("User", "A user of my software system.");

        SoftwareSystem softwareSystem = model.addSoftwareSystem("Software System", "My software system.");
        user.uses(softwareSystem, "Uses");
        return softwareSystem;
    }

    private ViewSet assignViews(Workspace workspace, SoftwareSystem softwareSystem) {
        // define some views (the diagrams you would like to see)
        ViewSet views = workspace.getViews();

        SystemContextView contextView = views.createSystemContextView(softwareSystem, "SystemContext", "An example of a System Context diagram.");
        contextView.setPaperSize(PaperSize.A5_Landscape);
        contextView.addAllSoftwareSystems();
        contextView.addAllPeople();
        return views;
    }

    private void assignDocumentation(Workspace workspace, SoftwareSystem softwareSystem) {
        // add some documentation
        StructurizrDocumentationTemplate template = new StructurizrDocumentationTemplate(workspace);
        template.addContextSection(softwareSystem, Format.Markdown,
                "Here is some context about the software system...\n" +
                        "\n" +
                        "![](embed:SystemContext)");
    }

    private void assignStyles(ViewSet views) {
        // add some styling
        Styles styles = views.getConfiguration().getStyles();
        styles.addElementStyle(Tags.SOFTWARE_SYSTEM).background("#1168bd").color("#ffffff");
        styles.addElementStyle(Tags.PERSON).background("#08427b").color("#ffffff").shape(Shape.Person);
    }
}
