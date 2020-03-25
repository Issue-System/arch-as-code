package net.trilogy.arch.generator;

import com.structurizr.model.Element;
import com.structurizr.model.IdGenerator;
import com.structurizr.model.Relationship;

import java.util.Optional;
import java.util.function.Function;

public class FunctionalIdGenerator implements IdGenerator {
    private String nextValue;
    private Function<Relationship, String> defaultFunctionForRelationships;

    public FunctionalIdGenerator() {
        this.nextValue = null;
        this.defaultFunctionForRelationships = null;
    }

    public void setNext(String id) {
        this.nextValue = id;
    }

    private Optional<String> consumeNextValue() {
        var temp = this.nextValue;
        this.nextValue = null;
        return Optional.ofNullable(temp);
    }

    private Optional<String> useDefaultFunctionForRelationships(Relationship r) {
        if (this.defaultFunctionForRelationships == null) {
            return Optional.empty();
        }
        return Optional.of(this.defaultFunctionForRelationships.apply(r));
    }

    @Override
    public String generateId(Element element) {
        return consumeNextValue().orElseThrow(NoNextIdSetException::new);
    }

    @Override
    public String generateId(Relationship relationship) {
        return consumeNextValue()
                .or(() -> useDefaultFunctionForRelationships(relationship))
                .orElseThrow(NoNextIdSetException::new);
    }

    @Override
    public void found(String id) {
    }

    public void setDefaultForRelationships(Function<Relationship, String> func) {
        this.defaultFunctionForRelationships = func;
    }

    public void clearDefaultForRelationships() {
        this.defaultFunctionForRelationships = null;
    }

    public static class NoNextIdSetException extends RuntimeException {
        public NoNextIdSetException() {
            super("Error: No next ID.");
        }
    }
}
