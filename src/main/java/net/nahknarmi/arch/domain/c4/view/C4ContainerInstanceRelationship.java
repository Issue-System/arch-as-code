package net.nahknarmi.arch.domain.c4.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class C4ContainerInstanceRelationship {
    private String id;
    private String containerInstanceId;
    private String description;
    private String technology;
}
