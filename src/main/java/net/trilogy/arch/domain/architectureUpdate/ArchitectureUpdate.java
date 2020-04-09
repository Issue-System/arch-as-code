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
        "requirements",
        "TDDs",
        "E2Es",
        "epic"
})
public class ArchitectureUpdate {
    private final String name;
    private final String milestone;
    private final List<Person> authors;
    private final List<Person> PCAs;
    private final Map<Requirement.Id, Requirement> requirements;
    private final Map<TDD.ComponentReference, List<TDD>> TDDs;
    private final List<String> E2Es;
    private final Epic epic;

    @JsonProperty(value = "P2")
    private final P2 p2;

    @JsonProperty(value = "P1")
    private final P1 p1;

    @JsonProperty(value = "useful-links")
    private final List<Link> usefulLinks;

    @JsonProperty(value = "milestone-dependencies")
    private final List<MilestoneDependency> milestoneDependencies;

    @Builder
    public ArchitectureUpdate(String name, String milestone, List<Person> authors, List<Person> PCAs, Map<Requirement.Id, Requirement> requirements, Map<TDD.ComponentReference, List<TDD>> TDDs, List<String> E2Es, Epic epic, P2 p2, P1 p1, List<Link> usefulLinks, List<MilestoneDependency> milestoneDependencies) {
        this.name = name;
        this.milestone = milestone;
        this.authors = authors;
        this.PCAs = PCAs;
        this.requirements = requirements;
        this.TDDs = TDDs;
        this.E2Es = E2Es;
        this.epic = epic;
        this.p2 = p2;
        this.p1 = p1;
        this.usefulLinks = usefulLinks;
        this.milestoneDependencies = milestoneDependencies;
    }

    public static ArchitectureUpdate blank() {
        return ArchitectureUpdate.builder()
                .name("[SAMPLE NAME]")
                .milestone("[SAMPLE MILESTONE]")
                .authors(List.of(Person.blank()))
                .PCAs(List.of(Person.blank()))
                .requirements(Map.of(Requirement.Id.blank(), Requirement.blank()))
                .TDDs(Map.of(TDD.ComponentReference.blank(), List.of(TDD.blank())))
                .E2Es(List.of("[SAMPLE E2E]"))
                .epic(Epic.blank())
                .p2(P2.blank())
                .p1(P1.blank())
                .usefulLinks(List.of(Link.blank()))
                .milestoneDependencies(List.of(MilestoneDependency.blank()))
                .build();
    }

}
