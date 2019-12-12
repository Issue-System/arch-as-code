package net.nahknarmi.arch.domain;


import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import net.nahknarmi.arch.domain.c4.C4Model;

import java.util.List;

import static net.nahknarmi.arch.domain.c4.C4Model.NONE;

@Data
@AllArgsConstructor
public class ArchitectureDataStructure {
    @NonNull private String name;
    @NonNull private Long id;
    @NonNull private String businessUnit;
    @NonNull private String description;
    @NonNull private List<ImportantTechnicalDecision> decisions = ImmutableList.of();
    @NonNull private C4Model model = NONE;

    ArchitectureDataStructure() {
    }
}
