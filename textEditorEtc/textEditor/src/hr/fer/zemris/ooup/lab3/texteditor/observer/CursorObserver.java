package hr.fer.zemris.ooup.lab3.texteditor.observer;

import hr.fer.zemris.ooup.lab3.texteditor.Location;

public interface CursorObserver {
    void updateCursorLocation(Location loc);
}
