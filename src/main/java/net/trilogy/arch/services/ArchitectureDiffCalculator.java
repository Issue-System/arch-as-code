package net.trilogy.arch.services;

import com.google.common.collect.Sets;
import net.trilogy.arch.domain.ArchitectureDataStructure;
import net.trilogy.arch.domain.Diff;
import net.trilogy.arch.domain.Diffable;
import net.trilogy.arch.domain.DiffableRelationship;
import net.trilogy.arch.domain.c4.C4Component;
import net.trilogy.arch.domain.c4.C4Container;
import net.trilogy.arch.domain.c4.C4SoftwareSystem;
import net.trilogy.arch.domain.c4.Entity;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ArchitectureDiffCalculator {
    public static Set<Diff> diff(ArchitectureDataStructure beforeArch, ArchitectureDataStructure afterArch) {
        final Set<Diff> firstDiffs = getAllDiffables(beforeArch).stream()
                .map(diffableInFirst -> {
                        var diffableInSecond = findById(afterArch, diffableInFirst.getId()).orElse(null);
                        return makeDiff(beforeArch, afterArch, diffableInFirst, diffableInSecond);
                    }
                )
                .collect(Collectors.toSet());

        final Set<Diff> secondDiffs = getAllDiffables(afterArch).stream()
                .map(thing2 -> {
                        var thing1 = findById(beforeArch, thing2.getId()).orElse(null);
                        return makeDiff(beforeArch, afterArch, thing1, thing2);
                    }
                )
                .collect(Collectors.toSet());

        return Sets.union(firstDiffs, secondDiffs);
    }

    private static Diff makeDiff(ArchitectureDataStructure firstArch,
                                 ArchitectureDataStructure secondArch,
                                 Diffable diffableInFirst,
                                 Diffable diffableInSecond) {
        return new Diff(
                diffableInFirst, getDescendants(diffableInFirst, firstArch),
                diffableInSecond, getDescendants(diffableInSecond, secondArch)
        );
    }

    private static Set<Diffable> getDescendants(Diffable diffable, ArchitectureDataStructure arch) {
        if(diffable instanceof C4SoftwareSystem) {
            Set<Diffable> results = new HashSet<>();
            Set<C4Container> containers = getContainers((C4SoftwareSystem) diffable, arch);
            for (Diffable container : containers) {
                results.add(container);
                results.addAll(getDescendants(container, arch));
            }
            return results;
        }
        if(diffable instanceof C4Container) {
            return new HashSet<>(getComponents((C4Container) diffable, arch));
        }
        return Set.of();
    }

    private static Set<C4Container> getContainers(C4SoftwareSystem system, ArchitectureDataStructure arch) {
        return arch.getModel().getContainers().stream().filter(it -> Objects.equals(it.getSystemId(), system.getId())).collect(Collectors.toSet());
    }

    private static Set<C4Component> getComponents(C4Container container, ArchitectureDataStructure arch) {
        return arch.getModel().getComponents().stream().filter(it -> Objects.equals(it.getContainerId(), container.getId())).collect(Collectors.toSet());
    }

    private static Optional<? extends Diffable> findById(ArchitectureDataStructure arch, String id) {
        return Stream.of(
                arch.getModel().findRelationshipById(id).map(it -> new DiffableRelationship(arch, it)),
                arch.getModel()
                        .findEntityById(id)
                        .map(ArchitectureDiffCalculator::clearRelationships)
            )
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findAny();
    }

    private static Set<Diffable> getAllDiffables(ArchitectureDataStructure arch) {
        return Sets.union(
                arch.getModel()
                    .allEntities()
                    .stream()
                    .map(ArchitectureDiffCalculator::clearRelationships)
                    .collect(Collectors.toSet()),

                arch.getModel()
                    .allRelationships()
                    .stream()
                    .map(entityRelationshipPair -> new DiffableRelationship(entityRelationshipPair._1, entityRelationshipPair._2))
                    .collect(Collectors.toSet())
        );
    }

    private static Entity clearRelationships(Entity entity) {
        var copy = entity.shallowCopy();
        copy.setRelationships(List.of());
        return copy;
    }
}
