package hr.fer.zemris.ooup.lab3.texteditor.command.impl;

import hr.fer.zemris.ooup.lab3.texteditor.command.EditAction;
import hr.fer.zemris.ooup.lab3.texteditor.Location;
import hr.fer.zemris.ooup.lab3.texteditor.LocationRange;
import hr.fer.zemris.ooup.lab3.texteditor.TextEditorModel;

import java.util.Iterator;

public class DeleteRangeAction implements EditAction {

    private final TextEditorModel textEditorModel;
    private String text;
    private final LocationRange locationRange;
    // private Location location;


    public DeleteRangeAction(TextEditorModel textEditorModel){
        this.textEditorModel = textEditorModel;
        LocationRange range = textEditorModel.getSelectionRange();
        Location start = range.getTextStart().compareTo(range.getTextEnd()) < 0 ? range.getTextStart() : range.getTextEnd();
        Location end = range.getTextStart().compareTo(range.getTextEnd()) > 0 ? range.getTextStart() : range.getTextEnd();
        this.locationRange = new LocationRange(new Location(start.getRow(), start.getColumn()), new Location(end.getRow(), end.getColumn()));
        textEditorModel.setSelectionRange(null);
    }
    @Override
    public void execute_do() {
        StringBuilder textSB;
        if (locationRange.getTextStart().getRow() != locationRange.getTextEnd().getRow()) {
            textSB = new StringBuilder(this.textEditorModel.getLines().get(locationRange.getTextStart().getRow()).substring(locationRange.getTextStart().getColumn()) + "\n");
            Iterator<String> iterator = this.textEditorModel.linesRange(locationRange.getTextStart().getRow() + 1, locationRange.getTextEnd().getRow());
            while(iterator.hasNext()) {
                textSB.append(iterator.next()).append("\n");
            }
            textSB.append(this.textEditorModel.getLines().get(locationRange.getTextEnd().getRow()), 0, locationRange.getTextEnd().getColumn());
        } else {
            textSB = new StringBuilder(this.textEditorModel.getLines().get(locationRange.getTextStart().getRow()).substring(locationRange.getTextStart().getColumn(),
                    locationRange.getTextEnd().getColumn()));
            
        }
        text = textSB.toString();
        textEditorModel.deleteRange(locationRange);
    }

    @Override
    public void execute_undo() {
        textEditorModel.setCursorLocation(locationRange.getTextStart().getRow(), locationRange.getTextStart().getColumn());
        textEditorModel.insert(text);
    }
}
