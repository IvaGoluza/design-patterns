import model.GUI;
import model.graphic.GraphicalObject;
import model.graphic.impl.LineSegment;
import model.graphic.impl.Oval;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<GraphicalObject> objects = new ArrayList<>();  // prototipi
        objects.add(new LineSegment());
        objects.add(new Oval());
        GUI gui = new GUI(objects);
        gui.setVisible(true);
    }

}
