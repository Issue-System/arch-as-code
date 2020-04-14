package net.trilogy.arch.domain.architectureUpdate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@ToString
@EqualsAndHashCode
@JsonPropertyOrder(value = {
        "name",
        "milestone",
        "authors",
        "PCAs",
        "P2",
        "P1",
        "useful-links",
        "milestone-dependencies",
        "decisions",
        "TDDs",
        "functional-requirements",
        "capabilities"
})
public class ArchitectureUpdate {
    private final String name;
    private final String milestone;
    private final List<Person> authors;
    private final List<Person> PCAs;
    private final Map<Decision.Id, Decision> decisions;
    private final Map<Tdd.ComponentReference, List<Tdd>> TDDs;

    @JsonProperty(value = "P2")
    private final P2 p2;

    @JsonProperty(value = "P1")
    private final P1 p1;

    @JsonProperty(value = "useful-links")
    private final List<Link> usefulLinks;

    @JsonProperty(value = "milestone-dependencies")
    private final List<MilestoneDependency> milestoneDependencies;

    @JsonProperty(value = "functional-requirements")
    private final Map<FunctionalRequirement.Id, FunctionalRequirement> functionalRequirements;

    @JsonProperty(value = "capabilities")
    private final CapabilitiesContainer capabilityContainer;

    @Builder
    public ArchitectureUpdate(String name,
                              String milestone,
                              List<Person> authors,
                              List<Person> PCAs,
                              Map<Decision.Id, Decision> decisions,
                              Map<Tdd.ComponentReference, List<Tdd>> TDDs,
                              Map<FunctionalRequirement.Id, FunctionalRequirement> functionalRequirements,
                              CapabilitiesContainer capabilityContainer,
                              P2 p2,
                              P1 p1,
                              List<Link> usefulLinks,
                              List<MilestoneDependency> milestoneDependencies) {
        this.name = name;
        this.milestone = milestone;
        this.authors = authors;
        this.PCAs = PCAs;
        this.decisions = decisions;
        this.TDDs = TDDs;
        this.functionalRequirements = functionalRequirements;
        this.capabilityContainer = capabilityContainer;
        this.p2 = p2;
        this.p1 = p1;
        this.usefulLinks = usefulLinks;
        this.milestoneDependencies = milestoneDependencies;
    }

    public static ArchitectureUpdateBuilder builderPreFilledWithBlanks() {
        return ArchitectureUpdate.builder()
                .name("[SAMPLE NAME]")
                .milestone("[SAMPLE MILESTONE]")
                .authors(List.of(Person.blank()))
                .PCAs(List.of(Person.blank()))
                .decisions(Map.of(Decision.Id.blank(), Decision.blank()))
                .TDDs(Map.of(Tdd.ComponentReference.blank(), List.of(Tdd.blank())))
                .functionalRequirements(Map.of(FunctionalRequirement.Id.blank(), FunctionalRequirement.blank()))
                .capabilityContainer(CapabilitiesContainer.blank())
                .p2(P2.blank())
                .p1(P1.blank())
                .usefulLinks(List.of(Link.blank()))
                .milestoneDependencies(List.of(MilestoneDependency.blank()));
    }

    public static ArchitectureUpdate blank() {
        return builderPreFilledWithBlanks().build();
    }

}
