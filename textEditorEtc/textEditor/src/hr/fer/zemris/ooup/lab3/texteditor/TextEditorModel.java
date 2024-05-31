package hr.fer.zemris.ooup.lab3.texteditor;

import hr.fer.zemris.ooup.lab3.texteditor.observer.CursorObserver;
import hr.fer.zemris.ooup.lab3.texteditor.observer.TextObserver;

import java.util.*;

public class TextEditorModel implements Iterable<String> {

    private List<String> lines;  // text lines
    private LocationRange selectionRange;  // coordinates (row, col) start and end of selected text
    private final Location cursorLocation;  // coordinates of current cursor location (coordinates of the letter in front of which the cursor is located)
    private final List<CursorObserver> cursorObservers;
    private final List<TextObserver> textObservers;
    public TextEditorModel(String initialText) {
        this.lines = new ArrayList<>(Arrays.asList(initialText.split("\n")));
        this.cursorLocation = new Location(this.lines.size()-1, this.lines.get(this.lines.size()-1).length());
        cursorObservers = new ArrayList<>();
        textObservers = new ArrayList<>();
    }

    public void subscribeCursorObserver(CursorObserver cursorObserver) {
        cursorObservers.add(cursorObserver);
    }

    public void unsubscribeCursorObserver(CursorObserver cursorObserver) {
        cursorObservers.remove(cursorObserver);
    }

    public void notifyCursorObservers() {
        cursorObservers.forEach(observer -> observer.updateCursorLocation(this.cursorLocation));
    }

    public void subscribeTextObserver(TextObserver textObserver) {
        textObservers.add(textObserver);
    }

    public void unsubscribeTextObserver(TextObserver textObserver) {
        textObservers.remove(textObserver);
    }

    public void notifyTextObservers() {
        textObservers.forEach(TextObserver::updateText);
    }

    public void moveCursorLeft() {
        int newCol = this.cursorLocation.getColumn() - 1;
        int row = this.cursorLocation.getRow();
        if(newCol < 0) {
            if(row - 1 < 0) return;
            setCursorLocation(row - 1, this.lines.get(row - 1).length());
        } else {
            setCursorLocation(row, newCol);
        }
        notifyCursorObservers();
    }

    public void moveCursorRight() {
        int newCol = this.cursorLocation.getColumn() + 1;
        int row = this.cursorLocation.getRow();
        if(newCol > this.lines.get(row).length()) {
            if(row + 1 >= this.lines.size()) return;
            setCursorLocation(row + 1, 0);
        } else {
            setCursorLocation(row, newCol);
        }
        notifyCursorObservers();
    }

    public void moveCursorUp() {
        int newRow = this.cursorLocation.getRow() - 1;
        int col = this.cursorLocation.getColumn();
        if(newRow >= 0) {
            int lineLen = this.lines.get(newRow).length();
            setCursorLocation(newRow, Math.min(lineLen, col));
            notifyCursorObservers();
        }
    }

    public void moveCursorDown() {
        int newRow = this.cursorLocation.getRow() + 1;
        int col = this.cursorLocation.getColumn();
        if(newRow < this.lines.size()) {
            int lineLen = this.lines.get(newRow).length();
            setCursorLocation(newRow, Math.min(lineLen, col));
            notifyCursorObservers();
        }
    }
    // deleteBefore: deletes the character behind the cursor and moves the cursor position backward (left)
    public void deleteBefore() {
        int cursorRow = this.cursorLocation.getRow();
        int cursorCol = this.cursorLocation.getColumn();
        if(cursorRow == 0 && cursorCol == 0) return;
        if(cursorCol == 0) {
            int newCursorCol = this.lines.get(cursorRow - 1).length();
            this.lines.set(cursorRow - 1, this.lines.get(cursorRow - 1) + this.lines.remove(cursorRow));
            setCursorLocation(cursorRow - 1, newCursorCol);
        } else {
            String oldLine = this.lines.get(cursorRow);
            this.lines.set(cursorRow, oldLine.substring(0, cursorCol - 1) + oldLine.substring(cursorCol));
            setCursorLocation(cursorRow, cursorCol - 1);
        }
        notifyTextObservers();
    }
    // deleteAfter: deletes the character in front of the cursor and does not change the cursor position
    public void deleteAfter() {
        int cursorRow = this.cursorLocation.getRow();
        int cursorCol = this.cursorLocation.getColumn();
        if(cursorRow == this.lines.size() - 1 && cursorCol == this.lines.get(this.lines.size() - 1).length()) return;
        
        
        if(cursorCol == this.lines.get(cursorRow).length()) {
            this.lines.set(cursorRow, this.lines.get(cursorRow) + this.lines.remove(cursorRow + 1));
        } else {
            String oldLine = this.lines.get(cursorRow);
            this.lines.set(cursorRow, oldLine.substring(0, cursorCol) + oldLine.substring(cursorCol + 1));
        }
        notifyTextObservers();
    }
    // deleteRange: deletes the given character range
    public void deleteRange(LocationRange r) {
        Location start = r.getTextStart().compareTo(r.getTextEnd()) < 0 ? r.getTextStart() : r.getTextEnd();
        Location end = r.getTextStart().compareTo(r.getTextEnd()) > 0 ? r.getTextStart() : r.getTextEnd();

        if(start.getRow() != end.getRow()) {
            String firstLinePart = this.lines.get(start.getRow()).substring(0, start.getColumn());
            String lastLinePart = this.lines.get(end.getRow()).substring(end.getColumn());

            int rowToDelete = start.getRow() + 1;
            while(rowToDelete <= end.getRow()) {
                this.lines.remove(start.getRow() + 1);
                rowToDelete++;
            }
            this.lines.set(start.getRow(), firstLinePart + lastLinePart);
            setCursorLocation(start.getRow(), firstLinePart.length());
        } else {
            if(this.lines.get(start.getRow()).length() >= end.getColumn()) {
                this.lines.set(start.getRow(), this.lines.get(start.getRow()).substring(0, start.getColumn()) + this.lines.get(start.getRow()).substring(end.getColumn()));
            } else {
                this.lines.set(start.getRow(), this.lines.get(start.getRow()).substring(0, start.getColumn()));
                
            }
            setCursorLocation(start.getRow(), start.getColumn());
        }
        notifyTextObservers();
    }
    // inserts a character at the cursor position and moves the cursor
    public void insert(char c) {
        if(this.selectionRange != null){
            deleteRange(this.selectionRange);
            this.selectionRange = null;
        }
        if(this.lines.size() == 0){
            this.lines.add(String.valueOf(c));
            notifyTextObservers();
            return;
        }
        String firstPart = this.lines.get(this.cursorLocation.getRow()).substring(0, this.cursorLocation.getColumn());
        String lastPart = this.lines.get(this.cursorLocation.getRow()).substring(this.cursorLocation.getColumn());

        if(c == '\n'){
            this.lines.set(this.cursorLocation.getRow(), firstPart);
            this.lines.add(this.cursorLocation.getRow() + 1,lastPart);
            setCursorLocation(this.cursorLocation.getRow()+1, 0);
        }else {
            this.lines.set(this.cursorLocation.getRow(), firstPart + c + lastPart);
            setCursorLocation(this.cursorLocation.getRow(), this.cursorLocation.getColumn() + 1);
        }
        notifyTextObservers();
    }
    // inserts arbitrary text (potentially multiple lines) at the cursor position and moves the cursor
    public void insert(String text) {
        for(char c : text.toCharArray())
            insert(c);
    }

    public LocationRange getSelectionRange() {
        return this.selectionRange;
    }

    public void setSelectionRange(LocationRange range) {
        this.selectionRange = range;
        notifyTextObservers();
    }
    public List<String> getLines() {
        return lines;
    }
    public void setLines(List<String> lines) {
        this.lines = lines;
        notifyTextObservers();
    }
    public Location getCursorLocation() {
        return cursorLocation;
    }

    public void setCursorLocation(int row, int col) {
        this.cursorLocation.setRow(row);
        this.cursorLocation.setColumn(col);
    }

    @Override
    public Iterator<String> iterator() {
        return new LinesIterator(this, 0, this.getLines().size());
    }

    public Iterator<String> allLines() {
        return iterator();
    }

    public Iterator<String> linesRange(int start, int end) {
        if (start < 0 || end < 0 || end >= lines.size())
            throw new IllegalArgumentException("Invalid index.");

        return new LinesIterator(this, start, end);
    }

    public void moveCursorToDocumentStart() {
        setCursorLocation(0, 0);
        notifyTextObservers();
    }

    public void moveCursorToDocumentEnd() {
        setCursorLocation(lines.size()-1, lines.get(lines.size()-1).length());
        notifyTextObservers();
    }

    public void clearDocument() {
        lines.clear();
    }

    private static class LinesIterator implements Iterator<String> {

        private final TextEditorModel textEditorModel;
        private int current = 0;
        private final int end;
        public LinesIterator(TextEditorModel textEditorModel, int start, int end) {
            this.textEditorModel = textEditorModel;
            this.current = start;
            this.end = end;
        }

        @Override
        public boolean hasNext() {
            return current < end;
        }

        @Override
        public String next() {
            if(!hasNext()) throw new NoSuchElementException();
            return textEditorModel.getLines().get(current++);
        }
    }
}
