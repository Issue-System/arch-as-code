package net.trilogy.arch.domain.architectureUpdate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.LinkedHashMap;
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
        this.authors = copyList(authors);
        this.PCAs = copyList(PCAs);
        this.requirements = copyMap(requirements);
        this.TDDs = copyMapWithList(TDDs);
        this.E2Es = copyList(E2Es);
        this.epic = epic;
        this.p2 = p2;
        this.p1 = p1;
        this.usefulLinks = copyList(usefulLinks);
        this.milestoneDependencies = copyList(milestoneDependencies);
    }

    public static ArchitectureUpdate blank() {
        Jira sampleJira = new Jira("[SAMPLE JIRA TICKET]", "[SAMPLE JIRA TICKET LINK]");
        return ArchitectureUpdate.builder()
                .name("[SAMPLE NAME]")
                .milestone("[SAMPLE MILESTONE]")
                .authors(List.of(new Person("[SAMPLE AUTHOR NAME]", "[SAMPLE AUTHOR EMAIL]")))
                .PCAs(List.of(new Person("[SAMPLE PCA NAME]", "[SAMPLE PCA EMAIL]")))
                .requirements(Map.of(new Requirement.Id("[SAMPLE-REQUIREMENT-ID]"), new Requirement("[SAMPLE REQUIREMENT TEXT]", List.of(new TDD.Id("[SAMPLE-TDD-ID]")))))
                .TDDs(Map.of(new TDD.ComponentReference("[SAMPLE-COMPONENT-ID]"), List.of(new TDD(new TDD.Id("[SAMPLE-TDD-ID]"), "[SAMPLE TDD TEXT]"))))
                .E2Es(List.of("[SAMPLE E2E]"))
                .epic(new Epic("[SAMPLE EPIC TITLE]", sampleJira, List.of(new Capability(
                        sampleJira,
                        List.of(new TDD.Id("[SAMPLE-TDD-ID]")),
                        List.of(new Requirement.Id("[SAMPLE-REQUIREMENT-ID]"))
                ))))
                .p2(new P2("[SAMPLE LINK TO P1]", sampleJira))
                .p1(new P1("[SAMPLE LINK TO P1]", sampleJira, "[SAMPLE EXECUTIVE SUMMARY]"))
                .usefulLinks(List.of(new Link("[SAMPLE USEFUL LINK DESCRIPTION]", "[SAMPLE-USEFUL-LINK]")))
                .milestoneDependencies(List.of(new MilestoneDependency("[SAMPLE MILESTONE DEPENDENCY]", List.of(new Link("[SAMPLE LINK DESCRIPTION]", "[SAMPLE-LINK]")))))
                .build();
    }

    private <TA, TB> Map<TA, TB> copyMap(Map<TA, TB> toCopy) {
        return toCopy != null ? new LinkedHashMap<>(toCopy) : new LinkedHashMap<>();
    }

    private <TA, TB> Map<TA, List<TB>> copyMapWithList(Map<TA, List<TB>> toCopy) {
        return toCopy != null ? new LinkedHashMap<>(toCopy) : new LinkedHashMap<>();
    }

    static <T> ArrayList<T> copyList(List<T> orig) {
        return orig != null ? new ArrayList<>(orig) : new ArrayList<>();
    }

}
