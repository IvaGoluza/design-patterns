package hr.fer.task6;

import hr.fer.task6.cell.impl.Cell;

import java.util.*;

public class Sheet {

    private final Cell[][] cells;

    private final int rows;

    private final int cols;

    public Sheet(int rows, int cols) {
        this.cells = new Cell[rows][cols];
        this.rows = rows;
        this.cols = cols;
    }

    public Cell cell(String ref) {                  // sheet.cell("A1") -> cells[0][0]
        if(ref == null || ref.length() < 2) {
            assert ref != null;
            throw new IllegalArgumentException("The cell reference is not valid." + ref + " " + ref.length());
        }

        int col = ref.charAt(0) - 'A';
        int row = Integer.parseInt(ref.substring(1)) - 1;

        if (col < 0 || row < 0 || row >= this.rows || col >= this.cols)
            throw new IllegalArgumentException("The cell reference is out of sheet bounds.");

        return this.cells[row][col];            // returns a wanted sheet cell
    }

    public void checkCircular(String ref, String content) {
        String[] cell_refs = content.split("\\+");
        for(String cell_ref : cell_refs) {
            try {
                Integer.parseInt(cell_ref);    // the expression is only a number without references
                break;
            } catch (NumberFormatException ignored) {}

            Cell cell = cell(cell_ref);
            if( cell != null && cell.getExp().contains(ref))
                throw new IllegalArgumentException("Circular cell reference is not possible!");
        }
    }

    public List<Cell> getRefs(Cell cell) {
        try {
            Integer.parseInt(cell.getExp());    // the expression is only a number without references
            return Collections.emptyList();
        } catch (NumberFormatException ignored) {}

        if (!cell.getExp().contains("+"))
            return Collections.singletonList(cell(cell.getExp()));   // the expression is just one cell reference

        String[] cell_refs = cell.getExp().split("\\+");


        return Arrays.stream(cell_refs)         // the expression has 2 cell references
                .map(this::cell)
                .filter(Objects::nonNull).toList();
    }

    public void set(String ref, String content) {    // cells[ref].exp = content if not circular
        if(ref == null || ref.length() < 2)
            throw new IllegalArgumentException("The cell reference is not valid.");

        int col = ref.charAt(0) - 'A';
        int row = Integer.parseInt(ref.substring(1)) - 1;

        if (col < 0 || row < 0 || row >= this.rows || col >= this.cols)
            throw new IllegalArgumentException("The cell reference is out of sheet bounds.");

        checkCircular(ref, content);

        if (this.cells[row][col] == null)
            this.cells[row][col] = new Cell(content);
        else {
            for(Cell was_depending_cell : getRefs(cells[row][col])) {     // the cell expression was already used, detach its old observers
                was_depending_cell.detach(cells[row][col]);
            }
            this.cells[row][col].setExp(content);
        }

        for(Cell depending_cell : getRefs(cells[row][col])) {  // cell has to observe for state changes of cells on which it depends by expression
            depending_cell.attach(cells[row][col]);
        }
        cells[row][col].update(this);     // calculate new value from new cell expression
    }

    public void evaluate(Cell cell) {
        cell.setValue(evalExp(cell.getExp()));
    }

    private int evalExp(String exp) {
        if (exp.contains("+")) {       // sum exp
            String[] refs = exp.split("\\+");
            return evalExp(refs[0]) + evalExp(refs[1]);
        } else {
            try {
                return Integer.parseInt(exp);  // number exp
            } catch (NumberFormatException ignored) {}

            Cell cell = cell(exp);  // exp is another cell's exp
            if (cell == null)
                throw new IllegalArgumentException();
            else
                return evalExp(cell.getExp());
        }
    }

    public void printSheet() {
        System.out.print("     ");
        for (int i = 0; i < this.cols; i++) {
            System.out.format("%8s", (char) ('A' + i));
        }
        System.out.println();

        for (int i = 0; i < this.rows; i++) {
            System.out.format("%4s", (char) ('1' + i));
            for (Cell value : cells[i]) {
                System.out.format("%8s", value);
            }
            System.out.println();
        }
    }


    public static void main(String[] args) {
        Sheet sheet = new Sheet(5, 5);
        sheet.printSheet();

        sheet.set("A1", "2");
        sheet.set("A2", "5");
        sheet.set("A3", "A1+A2");
        sheet.printSheet();
        sheet.set("A1", "4");
        sheet.set("A4", "A1+A3");

        try {
            sheet.set("A1", "A3");
        } catch (RuntimeException e) {
            System.out.println("Caught exception:" + e);
        }
        sheet.printSheet();
    }

}