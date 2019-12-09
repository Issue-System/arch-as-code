package net.nahknarmi.arch.adapter;

import net.nahknarmi.arch.model.ArchitectureDataStructure;
import net.nahknarmi.arch.model.ArchitectureDataStructureImporter;
import org.junit.Test;

public class YAMLToStructurizrE2E {


    @Test
    public void should_be_able_to_submit_generated_workspace_to_structurizr_api_and_save_changes() {
        //given
        //yaml file with arch data structurizr



        //when
        //transform yaml file to workspace json
        ArchitectureDataStructureImporter importer = new ArchitectureDataStructureImporter();
        ArchitectureDataStructure dataStructure = importer.load(null);


        //then
        //load generated json to workspace


        //submit json to struturizr


        //retrieve workspace from structurizr & ensure data was saved
    }
}
