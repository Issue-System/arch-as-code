package net.trilogy.arch.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class ArchitectureUpdate {
    private final String name;
    private final String milestone;
    private final List<Person> authors;
    private final List<Person> PCAs;

    @JsonProperty(value = "P2")
    private final P2 p2;

    @JsonProperty(value = "P1")
    private final P1 p1;

    @JsonProperty(value = "useful-links")
    private final List<Link> usefulLinks;

    @JsonProperty(value = "milestone-dependencies")
    private final List<MilestoneDependency> milestoneDependencies;

    public ArchitectureUpdate(String name, String milestone, List<Person> authors, List<Person> PCAs, P2 p2, P1 p1, List<Link> usefulLinks, List<MilestoneDependency> milestoneDependencies) {
        this.name = name;
        this.milestone = milestone;
        this.authors = copyList(authors);
        this.PCAs = copyList(PCAs);
        this.p2 = p2;
        this.p1 = p1;
        this.usefulLinks = copyList(usefulLinks);
        this.milestoneDependencies = copyList(milestoneDependencies);
    }

    private static <T> ArrayList<T> copyList(List<T> orig) {
        return orig != null ? new ArrayList<>(orig) : new ArrayList<>();
    }

    public static class P2 {
        private final String link;
        private final Jira jira;

        public P2(String link, Jira jira) {
            this.link = link;
            this.jira = jira;
        }
    }

    public static class P1 {
        private final String link;
        private final Jira jira;
        private final String summary;

        public P1(String link, Jira jira, String summary) {
            this.link = link;
            this.jira = jira;
            this.summary = summary;
        }
    }

    public static class MilestoneDependency {
        private final String description;
        private final List<Link> links;

        public MilestoneDependency(String description, List<Link> links) {
            this.description = description;
            this.links = links;
        }
    }
}
