package hr.fer.task5.source.impl;

import hr.fer.task5.source.Source;

import java.util.Scanner;

public class KeyboardSource implements Source {

    private final Scanner scanner = new Scanner(System.in);
    @Override
    public int getNumber() {
        System.out.println("Enter positive number:");
        if(scanner.hasNext()) return scanner.nextInt();
        else return -1;
    }
}
