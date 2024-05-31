package hr.fer.zemris.ooup.lab3.texteditor.plugin.impl;

import hr.fer.zemris.ooup.lab3.texteditor.ClipboardStack;
import hr.fer.zemris.ooup.lab3.texteditor.TextEditorModel;
import hr.fer.zemris.ooup.lab3.texteditor.UndoManager;
import hr.fer.zemris.ooup.lab3.texteditor.plugin.Plugin;

import javax.swing.*;
import java.util.Arrays;

public class Statistika implements Plugin {

    @Override
    public String getName() {
        return "Statistika";
    }

    @Override
    public String getDescription() {
        return "Plugin broji koliko ima redaka, riječi i slova u dokumentu i to prikazuje korisniku u dijalogu.";
    }

    @Override
    public void execute(TextEditorModel model, UndoManager undoManager, ClipboardStack clipboardStack) {

        int words = (int) model.getLines().stream()
                .flatMap(line -> Arrays.stream(line.split(" ")))
                .filter(word -> !word.trim().isEmpty())
                .count();

        int letters = model.getLines().stream()
                .flatMap(line -> Arrays.stream(line.split(" ")))
                .mapToInt(String::length)
                .sum();

        String statistics = "Row count: " + model.getLines().size() + "\nWord count: " + words + "\nLetter count: " + letters;
        JOptionPane.showMessageDialog(null, statistics, "Statistics", JOptionPane.INFORMATION_MESSAGE);
    }
}
