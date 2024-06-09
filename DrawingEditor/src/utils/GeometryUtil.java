package utils;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class GeometryUtil {

   // izračunaj euklidsku udaljenost između dvije točke
    public static double distanceFromPoint(Point point1, Point point2) {

        return sqrt(pow(point1.getX() - point2.getX(), 2) + pow(point1.getY() - point2.getY(), 2));

    }

    // Izračunaj koliko je točka P udaljena od linijskog segmenta određenog
    // početnom točkom S i završnom točkom E. Uočite: ako je točka P iznad/ispod
    // tog segmenta, ova udaljenost je udaljenost okomice spuštene iz P na S-E.
    // Ako je točka P "prije" točke S ili "iza" točke E, udaljenost odgovara
    // udaljenosti od P do početne/konačne točke segmenta.
    public static double distanceFromLineSegment(Point s, Point e, Point p) {

        double dist;
        if (p.getX() >= Math.min(s.getX(), e.getX()) && p.getX() <=  Math.max(s.getX(), e.getX())) {  // tocka ispod ili iznad segmenta
            int a = e.getY() - s.getY();
            int b = e.getX() - s.getX();
            int c = e.getX() * s.getY() - e.getY() * s.getX();
            dist = Math.abs(a * p.getX() - b * p.getY() + c) / Math.sqrt(pow(a, 2) + pow(b, 2)); // dist = |a*xp-b*yp+c|/sqrt(a^2+b^2)
        } else {
            dist = Math.min(distanceFromPoint(s, p), distanceFromPoint(e, p)); // tocka prije S/iza E
        }
        return dist;

    }

}
