package hr.fer.task5.observer.impl;

import hr.fer.task5.NumberSequence;
import hr.fer.task5.observer.ActionObserver;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class FileWriteObserver implements ActionObserver {
    @Override
    public void update(NumberSequence numberSequence) {
        try (Writer writer = new OutputStreamWriter(Files.newOutputStream(Paths.get("numbers.txt"), StandardOpenOption.CREATE, StandardOpenOption.APPEND), StandardCharsets.UTF_8)) {

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            writer.write("[" + dateTimeFormatter.format(now) + "]");
            String numbers = numberSequence.getNumbers()
                    .stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(";"));
            writer.write(numbers + "\n");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
