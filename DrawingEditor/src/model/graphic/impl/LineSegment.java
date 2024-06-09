package model.graphic.impl;

import RenderImplementor.Renderer;
import model.graphic.GraphicalObject;
import utils.GeometryUtil;
import utils.Point;
import utils.Rectangle;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import static java.lang.Math.min;

public class LineSegment extends AbstractGraphicalObject{

    public LineSegment() {
        this(new Point(0, 0), new Point(10, 0));
    }

    public LineSegment(Point start, Point end) {
        super(new Point[]{start, end});
    }

    @Override
    public Rectangle getBoundingBox() {
        Point hp1 = getHotPoint(0);
        Point hp2 = getHotPoint(1);

        int x = min(hp1.getX(), hp2.getX());
        int y = min(hp1.getY(), hp2.getY());

        int width = Math.abs(hp1.getX() - hp2.getX());
        int height = Math.abs(hp1.getY() - hp2.getY());
        return new Rectangle(x, y, width, height);
    }

    @Override
    public double selectionDistance(Point mousePoint) {
        return GeometryUtil.distanceFromLineSegment(getHotPoint(0), getHotPoint(1), mousePoint);
    }

    @Override
    public void render(Renderer r) {
        r.drawLine(getHotPoint(0), getHotPoint(1));
    }

    @Override
    public String getShapeName() {
        return "Linija";
    }

    @Override
    public GraphicalObject duplicate() {
        return new LineSegment(
                new Point(getHotPoint(0).getX(), getHotPoint(0).getY()),
                new Point(getHotPoint(1).getX(), getHotPoint(1).getY())
        );
    }

    @Override
    public String getShapeID() {
        return "@LINE";
    }

    @Override
    public void save(List<String> rows) {
        // @LINE x pa y početne točke i x pa y konačne točke
        rows.add(getShapeID() + " " + getHotPoint(0).getX() + " " + getHotPoint(0).getY() + " " + getHotPoint(1).getX() + " " + getHotPoint(1).getY());

    }

    @Override
    public void load(Stack<GraphicalObject> stack, String data) {

        int[] args = Arrays.stream(data.trim().split(" "))
                .mapToInt(Integer::parseInt)
                .toArray();

        if (args.length == 4) {
            GraphicalObject go = duplicate();
            go.setHotPoint(0, new Point(args[0], args[1]));
            go.setHotPoint(1, new Point(args[2], args[3]));
            stack.push(go);
        }
    }
}
