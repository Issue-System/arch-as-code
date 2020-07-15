package net.trilogy.arch.validation;

import net.trilogy.arch.domain.ArchitectureDataStructure;
import net.trilogy.arch.domain.c4.C4Model;
import net.trilogy.arch.domain.c4.C4Relationship;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;

public class RelationsValidator implements DataStructureValidator {

    @Override
    public List<String> validate(ArchitectureDataStructure dataStructure) {
        C4Model model = dataStructure.getModel();

        Stream<C4Relationship> systemRelations = model.getSystems().stream().flatMap(s -> s.getRelationships().stream());
        Stream<C4Relationship> containerRelations = model.getContainers().stream().flatMap(s -> s.getRelationships().stream());

        return concat(systemRelations, containerRelations).filter(r -> r.getTechnology() == null || r.getTechnology().isBlank())
                .map(r -> "Relation id " + r.getId() + " doesn't have required technology.").collect(toList());
    }
}
