package net.nahknarmi.arch.domain.c4.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class C4DeploymentNode {
    private String id;
    private String name;
    private String description;
    private String technology;
    private String environment;
    private Integer instances;
    private C4DeploymentNode parent;
    private List<C4DeploymentNode> children;
    private List<C4ContainerInstance> containerInstances;

}
