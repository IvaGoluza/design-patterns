package hr.fer.zemris.ooup.lab2.model.plugins;

import hr.fer.zemris.ooup.lab2.model.Animal;

public class Parrot extends Animal {

    private final String animalName;

    public Parrot(String name) {
        this.animalName = name;
    }

    @Override
    public String name() {
        return this.animalName;
    }

    @Override
    public String greet() {
        return "rim tim";
    }

    @Override
    public String menu() {
        return "cookies";
    }
}
