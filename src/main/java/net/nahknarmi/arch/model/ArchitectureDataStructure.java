package net.nahknarmi.arch.model;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ArchitectureDataStructure {
    private String name;
    private Long id;
    private String businessUnit;
    private String description;
    private List<ImportantTechnicalDecision> decisions;

    ArchitectureDataStructure() {
    }
}
