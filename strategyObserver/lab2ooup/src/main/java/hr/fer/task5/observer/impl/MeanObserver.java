package hr.fer.task5.observer.impl;

import hr.fer.task5.NumberSequence;
import hr.fer.task5.observer.ActionObserver;

import java.util.OptionalDouble;
public class MeanObserver implements ActionObserver {
    @Override
    public void update(NumberSequence numberSequence) {
        OptionalDouble mean = numberSequence.getNumbers().stream().mapToInt(Integer::intValue).average();
        System.out.println("Number sequence mean value: " + mean.orElse(0.0));
    }
}
