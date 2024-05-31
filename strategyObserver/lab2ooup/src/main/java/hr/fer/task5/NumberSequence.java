package hr.fer.task5;

import hr.fer.task5.observer.ActionObserver;
import hr.fer.task5.observer.impl.FileWriteObserver;
import hr.fer.task5.observer.impl.MeanObserver;
import hr.fer.task5.observer.impl.MedObserver;
import hr.fer.task5.observer.impl.SumObserver;
import hr.fer.task5.source.Source;
import hr.fer.task5.source.impl.FileSource;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class NumberSequence {   // [subject in observer design pattern] [context in strategy design pattern]
    private final Source source;
    private final List<Integer> numbers;  // subject's state
    private final List<ActionObserver> observers ;  // subscribers

    public NumberSequence(Source source) {
        this.source = source;
        this.numbers = new ArrayList<>();
        this.observers = new ArrayList<>();
    }

    public void start() {
        while (true) {
            int number = source.getNumber();
            if(number == -1) break;   // there is no more numbers
            if(number >= 0) changeState(number);   // save number if positive [subject's state has changed]
            notify_();   // notify all the subscribed observers
            try {
                Thread.sleep(1000);    // get new number every second
                System.out.println();
            } catch (InterruptedException ignorable) {
                break;
            }
        }
    }

    private void attach(ActionObserver observer) {
        this.observers.add(observer);
    }
    private void changeState(int number) {
        this.numbers.add(number);
    }
    private void notify_() {
        for (ActionObserver observer : observers)
            observer.update(this);
    }

    private void detach(ActionObserver observer) {
        this.observers.remove(observer);
    }

    public List<Integer> getNumbers() {
        return numbers;
    }

    public static void main(String[] args) throws FileNotFoundException {

        NumberSequence numberSequence = new NumberSequence(new FileSource("inputNumbers.txt"));    // options: KeyboardSource, FileSource("inputNumbers.txt")
        ActionObserver fileWriteObserver = new FileWriteObserver();
        ActionObserver sumObserver = new SumObserver();
        ActionObserver meanObserver = new MeanObserver();
        ActionObserver medObserver = new MedObserver();

        numberSequence.attach(fileWriteObserver);
        numberSequence.attach(sumObserver);
        numberSequence.attach(meanObserver);
        numberSequence.attach(medObserver);

        numberSequence.start();

        numberSequence.detach(fileWriteObserver);
        numberSequence.detach(sumObserver);
        numberSequence.detach(meanObserver);
        numberSequence.detach(medObserver);

        System.out.println("Current number sequence:");
        for(int i : numberSequence.getNumbers()) {
            System.out.print(i + " ");
        }
    }

}
