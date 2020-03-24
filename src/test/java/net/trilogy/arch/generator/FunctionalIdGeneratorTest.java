package net.trilogy.arch.generator;

import com.structurizr.model.*;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;


public class FunctionalIdGeneratorTest {
    private final FunctionalIdGenerator generator = new FunctionalIdGenerator();

    @Test
    public void shouldReturnIDFromAnonymousFunctionForElements() {
        generator.setNext("5");
        Person person = buildPerson();
        assertThat(generator.generateId(person), is(equalTo("5")));
    }

    @Test
    public void shouldReturnIDFromAnonymousFunctionForRelationships() {
        generator.setNext("300");

        Relationship relationship = buildRelationship();

        assertThat(generator.generateId(relationship), is(equalTo("300")));
    }

    private Person buildPerson() {
        return new Model().addPerson("abc", "def");
    }

    private Relationship buildRelationship() {
        Model model = new Model();
        Person person = model.addPerson("abc", "def");
        SoftwareSystem system = model.addSoftwareSystem("def", "ghi");
        return person.uses(system, "jkl");
    }

}
