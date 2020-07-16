package net.trilogy.arch.domain.c4.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Set;

import static java.util.Collections.emptySet;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class C4ViewContainer {
    public static final C4ViewContainer NONE = new C4ViewContainer();

    @NonNull
    private Set<C4SystemView> systemViews = emptySet();
    @NonNull
    private Set<C4ContainerView> containerViews = emptySet();
    @NonNull
    private Set<C4ComponentView> componentViews = emptySet();
    @NonNull
    private Set<C4DeploymentView> deploymentViews = emptySet();
}
