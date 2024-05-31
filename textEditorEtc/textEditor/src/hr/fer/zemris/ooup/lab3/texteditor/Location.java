package hr.fer.zemris.ooup.lab3.texteditor;

import java.util.Objects;

public class Location implements Comparable<Location>{

    private int row;
    private int column;

    public Location(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location location)) return false;
        return row == location.row && column == location.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }

    @Override
    public int compareTo(Location o) {
        if(this.equals(o)) return 0;

        if(this.row == o.row && this.column < o.column) return -1;

        if(this.row == o.row && this.column > o.column) return 1;

        if(this.row < o.row) return -1;

        else return 1;
    }
}
