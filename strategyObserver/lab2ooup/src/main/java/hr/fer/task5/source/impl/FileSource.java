package hr.fer.task5.source.impl;

import hr.fer.task5.source.Source;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FileSource implements Source {

    private final Scanner scanner;

    public FileSource(String path) throws FileNotFoundException {
        this.scanner = new Scanner(new File(path));;
    }

    @Override
    public int getNumber() {
        if(scanner.hasNext()) return scanner.nextInt();
        else return -1;
    }
}
