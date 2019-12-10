package net.nahknarmi.arch.model;


import java.util.List;

public class ArchitectureDataStructure {
    private String name;
    private Long id;
    private String businessUnit;
    private String description;
    private List<ImportantTechnicalDecision> decisions;

    public ArchitectureDataStructure() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBusinessUnit() {
        return businessUnit;
    }

    public void setBusinessUnit(String businessUnit) {
        this.businessUnit = businessUnit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ImportantTechnicalDecision> getDecisions() {
        return decisions;
    }

    public void setDecisions(List<ImportantTechnicalDecision> decisions) {
        this.decisions = decisions;
    }
}
