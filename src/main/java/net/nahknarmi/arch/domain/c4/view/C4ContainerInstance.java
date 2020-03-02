package net.nahknarmi.arch.domain.c4.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static java.util.Collections.emptyList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class C4ContainerInstance {
    private String id;
    private String environment;
    private String containerAlias;
    private String containerId;
    private List<C4ContainerInstanceRelationship> relationships = emptyList();
}
