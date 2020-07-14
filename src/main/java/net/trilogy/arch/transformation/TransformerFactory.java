package net.trilogy.arch.transformation;

import com.google.common.collect.ImmutableList;
import net.trilogy.arch.facade.FilesFacade;
import net.trilogy.arch.transformation.enhancer.*;

import java.io.File;

public abstract class TransformerFactory {

    public static ArchitectureDataStructureTransformer create(File documentRoot) {
        return new ArchitectureDataStructureTransformer(
                ImmutableList.of(
                        // TODO: Extact
                        new DocumentationEnhancer(documentRoot, new FilesFacade()),
                        new DecisionEnhancer(),
                        new ModelEnhancer()
//                        new StyleViewEnhancer(),
//                        new SystemContextViewEnhancer(),
//                        new ContainerContextViewEnhancer(),
//                        new ComponentContextViewEnhancer(),
//                        new SystemLandscapeViewEnhancer(),
//                        new DeploymentViewEnhancer()
                ));
    }
}
