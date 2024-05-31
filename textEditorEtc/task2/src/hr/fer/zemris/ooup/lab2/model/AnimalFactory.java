package hr.fer.zemris.ooup.lab2.model;

import java.lang.reflect.Constructor;


public class AnimalFactory {

    public static Animal newInstance(String animalKind, String name) throws Exception {

        Class<Animal> clazz = (Class<Animal>)Class.forName("hr.fer.zemris.ooup.lab2.model.plugins."+animalKind);
        Constructor<?> ctr = clazz.getConstructor(String.class);
		return (Animal)ctr.newInstance(name);
    }
}
