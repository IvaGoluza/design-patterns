package hr.fer.zemris.ooup.lab3.texteditor;

public class LocationRange {

    private final Location textStart;
    private final Location textEnd;

    public LocationRange(Location textStart, Location textEnd) {
        this.textStart = textStart;
        this.textEnd = textEnd;
    }

    public Location getTextStart() {
        return textStart;
    }

    public Location getTextEnd() {
        return textEnd;
    }
}
