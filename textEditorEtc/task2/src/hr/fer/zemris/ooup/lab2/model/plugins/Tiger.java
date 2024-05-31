package hr.fer.zemris.ooup.lab2.model.plugins;

import hr.fer.zemris.ooup.lab2.model.Animal;

public class Tiger extends Animal {

    private final String animalName;

    public Tiger(String name) {
        this.animalName = name;
    }

    @Override
    public String name() {
        return this.animalName;
    }

    @Override
    public String greet() {
        return "roar";
    }

    @Override
    public String menu() {
        return "apple";
    }
}
