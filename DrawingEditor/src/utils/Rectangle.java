package utils;

public class Rectangle {

    private final int x;
    private final int y;
    private final int width;
    private final int height;

    public Rectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Rectangle union(Rectangle goBBox) {

        int minX = Math.min(this.x, goBBox.getX());
        int minY = Math.min(this.y, goBBox.getY());
        int maxX = Math.max(this.x + this.width, goBBox.getX() + goBBox.getWidth());
        int maxY = Math.max(this.y + this.height, goBBox.getY() + goBBox.getHeight());

        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }
}
