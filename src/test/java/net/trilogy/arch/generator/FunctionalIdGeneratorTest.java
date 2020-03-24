package net.trilogy.arch.generator;

import com.structurizr.model.*;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;


public class FunctionalIdGeneratorTest {

    @Test
    public void shouldReturnIDFromAnonymousFunctionForElements() {
        final FunctionalIdGenerator generator = new FunctionalIdGenerator();
        generator.setNext("5");
        assertThat(generator.generateId(newPerson()), is(equalTo("5")));
    }

    @Test
    public void shouldReturnIDFromAnonymousFunctionForRelationships() {
        final FunctionalIdGenerator generator = new FunctionalIdGenerator();
        generator.setNext("300");
        assertThat(generator.generateId(newRelationship()), is(equalTo("300")));
    }

    @Test(expected = FunctionalIdGenerator.NoNextIdSetException.class)
    public void shouldNotGenerateIDUnlessSet() {
        final FunctionalIdGenerator generator = new FunctionalIdGenerator();
        generator.generateId(newRelationship());
    }

    @Test(expected = FunctionalIdGenerator.NoNextIdSetException.class)
    public void shouldNotGenerateIDTwiceIfSetOnce() {
        final FunctionalIdGenerator generator = new FunctionalIdGenerator();
        generator.setNext("300");
        generator.generateId(newPerson());
        generator.generateId(newPerson());
    }

    private Person newPerson() {
        return new Model().addPerson("abc", "def");
    }

    private Relationship newRelationship() {
        Model model = new Model();
        Person person = model.addPerson("abc", "def");
        SoftwareSystem system = model.addSoftwareSystem("def", "ghi");
        return person.uses(system, "jkl");
    }

}
