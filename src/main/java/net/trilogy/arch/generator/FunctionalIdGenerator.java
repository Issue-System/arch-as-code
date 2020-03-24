package net.trilogy.arch.generator;

import com.structurizr.model.ContainerInstance;
import com.structurizr.model.Element;
import com.structurizr.model.IdGenerator;
import com.structurizr.model.Relationship;

import java.util.function.Supplier;

public class FunctionalIdGenerator implements IdGenerator {
    private Supplier<String> generatorFunction;

    public FunctionalIdGenerator() {
    }

    public void setNext(String id) {
        this.generatorFunction = () -> id;
    }

    @Override
    public String generateId(Element element) {
//        C4Type c4Type = C4Type.from(element);
//
//        if (c4Type.equals(C4Type.containerInstance)) {
//            return makeContainerInstanceId(element);
//        }

        return this.generatorFunction.get();
    }

    private String makeContainerInstanceId(Element element) {
        String id = ((ContainerInstance) element).getContainer().getId() + "-" + ((ContainerInstance) element).getInstanceId();
        return id;
    }

    @Override
    public String generateId(Relationship relationship) {
        return this.generatorFunction.get();
    }

    @Override
    public void found(String id) {
    }

}
