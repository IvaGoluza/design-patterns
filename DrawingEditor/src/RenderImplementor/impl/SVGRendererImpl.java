package RenderImplementor.impl;

import RenderImplementor.Renderer;
import utils.Point;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SVGRendererImpl implements Renderer {

    private final List<String> lines = new ArrayList<>();
    private final String fileName;

    public SVGRendererImpl(String fileName) {
        this.fileName = fileName;
        // svg zaglavlje; dimenzije svg platna kao dimenzije drawing canvasa
        // lines.add("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<svg width=\"1000\" height=\"600\">\n");
        this.lines.add("<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" width=\"1000\" height=\"600\">");

    }

    public void close() throws IOException {
        lines.add("\n</svg>\n"); // završni tag SVG dokumenta: </svg>

        // sve retke u listi lines zapisuju se na disk u datoteku
        Path filePath = Path.of(fileName);
        Files.deleteIfExists(filePath);
        Files.createFile(filePath);
        Files.write(filePath, lines);
    }

    @Override
    public void drawLine(Point s, Point e) {
        // Dodaj u lines redak koji definira linijski segment:
        lines.add("<line x1=\"" + s.getX() + "\" y1=\"" + s.getY() + "\" x2=\"" + e.getX() + "\" y2=\"" + e.getY() + "\" style=\"stroke:rgb(0, 0, 255); stroke-width:2\" />");
    }

    @Override
    public void fillPolygon(Point[] points) {
        // Dodaj u lines redak koji definira popunjeni poligon:
        StringBuilder sb = new StringBuilder("\n<polygon points=\"");

        for(int i = 0; i < points.length; i++){
            sb.append(points[i].getX()).append(",").append(points[i].getY()).append(" ");
            if (i != points.length - 1) sb.append(" ");
        }

        sb.append("\" style=\"stroke:rgb(255, 0, 5); fill:rgb(0, 0, 255);\"/>\n");
        lines.add(sb.toString());
    }

}
