package hr.fer.zemris.ooup.lab2.model.hr.fer.zemris.ooup.lab2.model;

import hr.fer.zemris.ooup.lab2.model.Animal;
import hr.fer.zemris.ooup.lab2.model.AnimalFactory;

public class MainZoo {

    public static void main(String[] args) throws Exception {
        Animal animal = AnimalFactory.newInstance("Parrot", "Papiga");
        animal.animalPrintGreeting();
        animal.animalPrintMenu();
        animal = AnimalFactory.newInstance("Tiger", "Tigar");
        animal.animalPrintGreeting();
        animal.animalPrintMenu();
    }

}
