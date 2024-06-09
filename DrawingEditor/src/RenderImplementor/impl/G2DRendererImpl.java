package RenderImplementor.impl;

import RenderImplementor.Renderer;
import utils.Point;

import java.awt.*;
import java.util.Arrays;

public class G2DRendererImpl implements Renderer {

    // Renderer koji crtanje obavlja na površini grafičke komponente
    private final Graphics2D g2d;

    public G2DRendererImpl(Graphics2D g2d) {
       this.g2d = g2d;
    }
    @Override
    public void drawLine(Point s, Point e) {
        g2d.setColor(Color.BLUE); // Postavi boju na plavu
        g2d.drawLine(s.getX(), s.getY(), e.getX(), e.getY()); // Nacrtaj linijski segment od S do E
    }

    @Override
    public void fillPolygon(Point[] points) {

        g2d.setColor(Color.BLUE);  // Postavi boju na plavu
        int[] pointsX = Arrays.stream(points).mapToInt(Point::getX).toArray();
        int[] pointsY = Arrays.stream(points).mapToInt(Point::getY).toArray();
        g2d.fillPolygon(pointsX,pointsY,points.length);  // Popuni poligon definiran danim točkama

        g2d.setColor(Color.RED); // Postavi boju na crvenu
        g2d.drawPolygon(pointsX, pointsY, points.length);  // Nacrtaj rub poligona definiranog danim točkama

    }
}
