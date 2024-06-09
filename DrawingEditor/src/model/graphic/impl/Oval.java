package model.graphic.impl;

import RenderImplementor.Renderer;
import model.graphic.GraphicalObject;
import utils.GeometryUtil;
import utils.Point;
import utils.Rectangle;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class Oval extends AbstractGraphicalObject{

    // this.hotPoints = [rightHP, bottomHP]

    public Oval() {
        super(new Point[] { new Point(10, 0), new Point(0, 10) });
    }

    public Oval(Point rightHP, Point bottomHP){
        super(new Point[] {rightHP,bottomHP});
    }

    @Override
    public Rectangle getBoundingBox() {

        int halfAxis_a = Math.abs(getHotPoint(0).getX() - getHotPoint(1).getX());
        int halfAxis_b = Math.abs(getHotPoint(0).getY() - getHotPoint(1).getY());

        int width = 2 * halfAxis_a;
        int height = 2 * halfAxis_b;

        int x = getHotPoint(1).getX() - halfAxis_a;
        int y = getHotPoint(0).getY() - halfAxis_b;

        return new Rectangle(x,y,width, height);

    }

    @Override
    public double selectionDistance(Point mousePoint) {

        Point center = new Point(getHotPoint(1).getX(), getHotPoint(0).getY());

        int halfAxisA = Math.abs(getHotPoint(0).getX() - getHotPoint(1).getX());
        int halfAxisB = Math.abs(getHotPoint(0).getY() - getHotPoint(1).getY());

        // ((x-h)^2/a^2) + ((y-k)^2/b^2) <= 1; mousePoint(h,k)
        boolean isMouseInOval = ((Math.pow(mousePoint.getX() - center.getX(), 2) / Math.pow(halfAxisA, 2)) +
                (Math.pow(mousePoint.getY() - center.getY(), 2) / Math.pow(halfAxisB, 2))) <= 1;

        if (isMouseInOval)
            return 0.0;
        else
            return Math.min(GeometryUtil.distanceFromPoint(mousePoint, getHotPoint(0)),
                GeometryUtil.distanceFromPoint(mousePoint, getHotPoint(1)));

    }

    @Override
    public void render(Renderer r) {

        Point center = new Point(getHotPoint(1).getX(), getHotPoint(0).getY());

        int halfAxisA = Math.abs(getHotPoint(0).getX() - getHotPoint(1).getX());
        int halfAxisB = Math.abs(getHotPoint(0).getY() - getHotPoint(1).getY());

        Point[] points = new Point[180];

        for(int i = 0; i < 180; i++){
            double angle = 2 * Math.PI * i / 180;
            int x = center.getX() + (int)(halfAxisA * Math.cos(angle));
            int y = center.getY() + (int)(halfAxisB * Math.sin(angle));
            points[i] = new Point(x,y);
        }
        r.fillPolygon(points);
    }


    @Override
    public String getShapeName() {
        return "Oval";
    }

    @Override
    public GraphicalObject duplicate() {
        return new Oval(
                new Point(getHotPoint(0).getX(), getHotPoint(0).getY()),
                new Point(getHotPoint(1).getX(), getHotPoint(1).getY())
        );
    }

    @Override
    public String getShapeID() {
        return "@OVAL";
    }

    @Override
    public void save(List<String> rows) {
        // @OVAL x pa y desnog "hot-point"-a te x pa y donjeg "hot-point"-a
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
