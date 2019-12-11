package net.nahknarmi.arch;

import net.nahknarmi.arch.publish.ArchitectureDataStructurePublisher;

import java.io.File;

public class Bootstrap {
    private static final int PRODUCTION_WORKSPACE = 49328;
    private static final String PRODUCT_DOCUMENTATION_ROOT = "./documentation/products/";
    private static final String PRODUCT_NAME = "DevSpaces";

    public static void main(String[] args) throws Exception {
        new ArchitectureDataStructurePublisher(new File(PRODUCT_DOCUMENTATION_ROOT))
                .publish(PRODUCTION_WORKSPACE, PRODUCT_NAME);
    }
}
