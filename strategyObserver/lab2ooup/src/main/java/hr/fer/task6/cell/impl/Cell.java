package hr.fer.task6.cell.impl;

import hr.fer.task6.Sheet;
import hr.fer.task6.cell.Observer;

import java.util.ArrayList;
import java.util.List;

public class Cell implements Observer {
    private String exp;                        // expression: "5" or "A1+A2"
    private int value = Integer.MAX_VALUE;
    private final List<Observer> referencedCells = new ArrayList<>();    // cells from expression

    public Cell(String exp) {
        this.exp = exp;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    @Override
    public void update(Sheet sheet) {
        sheet.evaluate(this);
        for(Observer refCell : this.referencedCells){
            refCell.update(sheet);
        }
    }

    public void attach(Observer cell) {
        this.referencedCells.add(cell);
    }

    public void detach(Observer cell) {
        this.referencedCells.remove(cell);
    }

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
