package hr.fer.task5.observer.impl;

import hr.fer.task5.NumberSequence;
import hr.fer.task5.observer.ActionObserver;

public class SumObserver implements ActionObserver {
    @Override
    public void update(NumberSequence numberSequence) {
        int sum = numberSequence.getNumbers().stream().mapToInt(Integer::intValue).sum();
        System.out.println("Number sequence sum value: " + sum);
    }
}
