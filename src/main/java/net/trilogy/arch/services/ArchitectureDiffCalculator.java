package net.trilogy.arch.services;

import com.google.common.collect.Sets;
import io.vavr.Tuple2;
import net.trilogy.arch.domain.ArchitectureDataStructure;
import net.trilogy.arch.domain.Diff;
import net.trilogy.arch.domain.Diffable;
import net.trilogy.arch.domain.c4.C4Component;
import net.trilogy.arch.domain.c4.C4Container;
import net.trilogy.arch.domain.c4.C4SoftwareSystem;
import net.trilogy.arch.domain.c4.Entity;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ArchitectureDiffCalculator {
    public static Set<Diff> diff(ArchitectureDataStructure firstArch, ArchitectureDataStructure secondArch) {
        final Set<Diff> firstDiffs = getAllThings(firstArch).stream()
                .map(thing1 -> {
                        var thing2 = findById(secondArch, thing1.getId()).orElse(null);
                        return new Diff(
                            thing1, getDescendants(thing1, firstArch),
                            thing2, getDescendants(thing2, secondArch)
                        );
                    }
                )
                .collect(Collectors.toSet());

        final Set<Diff> secondDiffs = getAllThings(secondArch).stream()
                .map(thing2 -> {
                        var thing1 = findById(firstArch, thing2.getId()).orElse(null);
                        return new Diff(
                            thing1, getDescendants(thing1, firstArch),
                            thing2, getDescendants(thing2, secondArch)
                        );
                    }
                )
                .collect(Collectors.toSet());

        return Sets.union(firstDiffs, secondDiffs);
    }

    private static Set<Diffable> getDescendants(Diffable thing, ArchitectureDataStructure arch) {
        if(thing instanceof C4SoftwareSystem) {
            Set<Diffable> results = new HashSet<>();
            Set<Diffable> containers = getContainers((C4SoftwareSystem) thing, arch);
            for (Diffable container : containers) {
                results.add(container);
                results.addAll(getComponents((C4Container) container, arch));
            }
            return results;
        }
        if(thing instanceof C4Container) {
            return getComponents((C4Container) thing, arch);
        }
        return Set.of();
    }

    private static Set<Diffable> getContainers(C4SoftwareSystem system, ArchitectureDataStructure arch) {
        return arch.getModel().getContainers().stream().filter(it -> Objects.equals(it.getSystemId(), system.getId())).collect(Collectors.toSet());
    }

    private static Set<Diffable> getComponents(C4Container container, ArchitectureDataStructure arch) {
        return arch.getModel().getComponents().stream().filter(it -> Objects.equals(it.getContainerId(), container.getId())).collect(Collectors.toSet());
    }

    private static Optional<? extends Diffable> findById(ArchitectureDataStructure arch, String id) {
        return Stream.of(
                arch.getModel().findRelationshipById(id),
                arch.getModel()
                        .findEntityById(id)
                        .map(ArchitectureDiffCalculator::clearRelationships)
            )
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findAny();
    }

    private static Set<Diffable> getAllThings(ArchitectureDataStructure arch) {
        return Sets.union(
                arch.getModel()
                    .allEntities()
                    .stream()
                    .map(ArchitectureDiffCalculator::clearRelationships)
                    .collect(Collectors.toSet()),

                arch.getModel()
                    .allRelationships()
                    .stream()
                    .map(Tuple2::_2)
                    .collect(Collectors.toSet())
        );
    }

    private static Entity clearRelationships(Entity entity) {
        var copy = entity.shallowCopy();
        copy.setRelationships(List.of());
        return copy;
    }
}
