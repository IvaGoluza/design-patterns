package utils;

public class Point {

    private final int x;
    private final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Point translate(Point dp) {
        // vraća NOVU točku translatiranu za argument tj. THIS+DP...
        return new Point(x + dp.x, y + dp.y);
    }

    public Point difference(Point p) {
        // vraća NOVU točku koja predstavlja razliku THIS-P...
        return new Point(x - p.x, y - p.y);
    }

}
