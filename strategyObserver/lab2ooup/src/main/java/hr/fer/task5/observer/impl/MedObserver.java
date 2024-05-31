package hr.fer.task5.observer.impl;

import hr.fer.task5.NumberSequence;
import hr.fer.task5.observer.ActionObserver;

import java.util.List;

public class MedObserver implements ActionObserver {
    @Override
    public void update(NumberSequence numberSequence) {
        List<Integer> numSorted = numberSequence.getNumbers().stream().sorted().toList();
        double med;
        int size = numberSequence.getNumbers().size();
        if (size % 2 == 0) med = (numSorted.get(size / 2 - 1) + numSorted.get(size / 2)) / 2.0;
        else med = numSorted.get(size / 2);
        System.out.println("Number sequence median: " + med);
    }
}
