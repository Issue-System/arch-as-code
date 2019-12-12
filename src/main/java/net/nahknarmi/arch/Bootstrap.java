package net.nahknarmi.arch;

import net.nahknarmi.arch.publish.ArchitectureDataStructurePublisher;

import java.io.File;

public class Bootstrap {
    private static final String PRODUCT_DOCUMENTATION_ROOT = "./documentation/products/";
    private static final String PRODUCT_NAME = "arch-as-code";

    public static void main(String[] args) throws Exception {
        File root = new File(PRODUCT_DOCUMENTATION_ROOT);
        ArchitectureDataStructurePublisher.create(root).publish(PRODUCT_NAME);
    }
}
