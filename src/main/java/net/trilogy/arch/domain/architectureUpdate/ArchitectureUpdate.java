package net.trilogy.arch.domain.architectureUpdate;

import com.fasterxml.jackson.annotation.JsonCreator;
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
    @JsonProperty(value = "name") private final String name;
    @JsonProperty(value = "milestone") private final String milestone;
    @JsonProperty(value = "authors") private final List<Person> authors;
    @JsonProperty(value = "PCAs") private final List<Person> PCAs;
    @JsonProperty(value = "P2") private final P2 p2;
    @JsonProperty(value = "P1") private final P1 p1;
    @JsonProperty(value = "useful-links") private final List<Link> usefulLinks;
    @JsonProperty(value = "milestone-dependencies") private final List<MilestoneDependency> milestoneDependencies;
    @JsonProperty(value = "decisions") private final Map<Decision.Id, Decision> decisions;
    @JsonProperty(value = "TDDs") private final Map<Tdd.ComponentReference, List<Tdd>> TDDs;
    @JsonProperty(value = "functional-requirements") private final Map<FunctionalRequirement.Id, FunctionalRequirement> functionalRequirements;
    @JsonProperty(value = "capabilities") private final CapabilitiesContainer capabilityContainer;

    @Builder
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public ArchitectureUpdate(
            @JsonProperty("name") String name,
            @JsonProperty("milestone") String milestone,
            @JsonProperty("authors") List<Person> authors,
            @JsonProperty("PCAs") List<Person> PCAs,
            @JsonProperty("decisions") Map<Decision.Id, Decision> decisions,
            @JsonProperty("TDDs") Map<Tdd.ComponentReference, List<Tdd>> TDDs,
            @JsonProperty("functional-requirements") Map<FunctionalRequirement.Id, FunctionalRequirement> functionalRequirements,
            @JsonProperty("capabilities") CapabilitiesContainer capabilityContainer,
            @JsonProperty("p2") P2 p2,
            @JsonProperty("p1") P1 p1,
            @JsonProperty("useful-links") List<Link> usefulLinks,
            @JsonProperty("milestone-dependencies") List<MilestoneDependency> milestoneDependencies
    ) {
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
