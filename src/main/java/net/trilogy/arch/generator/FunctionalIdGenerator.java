package net.trilogy.arch.generator;

import com.structurizr.model.ContainerInstance;
import com.structurizr.model.Element;
import com.structurizr.model.IdGenerator;
import com.structurizr.model.Relationship;

import java.util.function.Supplier;

public class FunctionalIdGenerator implements IdGenerator {
    private Supplier<String> generatorFunction;

    public FunctionalIdGenerator() {
        this.setFunctionToDefault();
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

        return generateNextId();
    }

    @Override
    public String generateId(Relationship relationship) {
        return generateNextId();
    }

    @Override
    public void found(String id) {
    }

    private String generateNextId() {
        String id = this.generatorFunction.get();
        this.setFunctionToDefault();
        return id;
    }

    private void setFunctionToDefault() {
        this.generatorFunction = () -> {
            throw new NoNextIdSetException();
        };
    }

    private String makeContainerInstanceId(Element element) {
        String id = ((ContainerInstance) element).getContainer().getId() + "-" + ((ContainerInstance) element).getInstanceId();
        return id;
    }

    public static class NoNextIdSetException extends RuntimeException {
        public NoNextIdSetException(){
            super("Error: No next ID.");
        }
    }
}
