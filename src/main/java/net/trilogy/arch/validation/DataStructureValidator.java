package net.trilogy.arch.validation;

import net.trilogy.arch.domain.ArchitectureDataStructure;

import java.util.List;

interface DataStructureValidator {
    List<String> validate(ArchitectureDataStructure dataStructure);
}
