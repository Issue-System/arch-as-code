package net.trilogy.arch.domain.architectureUpdate;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@EqualsAndHashCode
public class ArchitectureUpdate {
    private final String name;
    private final String milestone;
    private final List<Person> authors;
    private final List<Person> PCAs;
    private final Map<Requirement.Id, Requirement> requirements;

    @JsonProperty(value = "P2")
    private final P2 p2;

    @JsonProperty(value = "P1")
    private final P1 p1;

    @JsonProperty(value = "useful-links")
    private final List<Link> usefulLinks;

    @JsonProperty(value = "milestone-dependencies")
    private final List<MilestoneDependency> milestoneDependencies;

    @Builder
    public ArchitectureUpdate(String name, String milestone, List<Person> authors, List<Person> PCAs, Map<Requirement.Id, Requirement> requirements, P2 p2, P1 p1, List<Link> usefulLinks, List<MilestoneDependency> milestoneDependencies) {
        this.name = name;
        this.milestone = milestone;
        this.authors = copyList(authors);
        this.PCAs = copyList(PCAs);
        this.requirements = copyMap(requirements);
        this.p2 = p2;
        this.p1 = p1;
        this.usefulLinks = copyList(usefulLinks);
        this.milestoneDependencies = copyList(milestoneDependencies);
    }

    public static ArchitectureUpdate blank() {
        return new ArchitectureUpdate(
                "",
                "",
                List.of(new Person("", "")),
                List.of(new Person("", "")),
                Map.of(new Requirement.Id("ITD 1.1"), new Requirement("requirement")),
                new P2("", new Jira("", "")),
                new P1("", new Jira("", ""), ""),
                List.of(new Link("", "")),
                List.of(new MilestoneDependency("", List.of(new Link("", ""))))
        );
    }

    private <TA, TB> Map<TA, TB> copyMap(Map<TA, TB> toCopy) {
        return toCopy != null ? new LinkedHashMap<>(toCopy) : new LinkedHashMap<>();
    }

    private static <T> ArrayList<T> copyList(List<T> orig) {
        return orig != null ? new ArrayList<>(orig) : new ArrayList<>();
    }

}
