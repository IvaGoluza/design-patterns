package RenderImplementor;

import utils.Point;

public interface Renderer {

    void drawLine(Point s, Point e);
    void fillPolygon(Point[] points);

}
