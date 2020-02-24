package net.nahknarmi.arch.domain.c4;

import org.junit.Test;

public class C4ModelTest {

    @Test(expected = IllegalArgumentException.class)
    public void fails_to_add_same_person_twice() {
        new C4Model()
                .addPerson(C4Person.builder().name("foo").description("bar").id("1").build())
                .addPerson(C4Person.builder().name("foo").description("bar").id("1").build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void fails_to_add_same_system_twice() {
        new C4Model()
                .addSoftwareSystem(C4SoftwareSystem.builder().name("foo").description("bar").id("1").build())
                .addSoftwareSystem(C4SoftwareSystem.builder().name("foo").description("bar").id("1").build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void fails_to_add_same_container_twice() {
        new C4Model()
                .addContainer(C4Container.builder().name("foo").description("bar").id("1").technology("tech").build())
                .addContainer(C4Container.builder().name("foo").description("bar").id("1").technology("tech").build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void fails_to_add_same_component_twice() {
        new C4Model()
                .addComponent(C4Component.builder().name("foo").description("bar").id("1").technology("tech").build())
                .addComponent(C4Component.builder().name("foo").description("bar").id("1").technology("tech").build());
    }

    @Test
    public void should_add_person_if_it_doesnt_already_exist_in_model() {
        new C4Model()
                .addPerson(C4Person.builder().name("foo").description("bar").id("1").build());
    }

    @Test
    public void should_add_system_if_it_doesnt_already_exist_in_model() {
        new C4Model()
                .addSoftwareSystem(C4SoftwareSystem.builder().name("foo").description("bar").id("1").build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void fails_to_add_two_people_with_same_name() {
        new C4Model()
                .addPerson(C4Person.builder().name("John").description("bar").id("1").build())
                .addPerson(C4Person.builder().name("John").description("bar").id("2").build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void fails_to_add_two_systems_with_same_name() {
        new C4Model()
                .addSoftwareSystem(C4SoftwareSystem.builder().name("OBP").description("bar").id("1").build())
                .addSoftwareSystem(C4SoftwareSystem.builder().name("OBP").description("bar").id("2").build());
    }
}
