import com.lh.component.common.Point;
import com.lh.component.common.Polyline;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PolylineTest {
    @Test
    public void singleLine() {
        Polyline polyline = new Polyline();
        polyline.addPoint(0, 0);
        polyline.addPoint(100, 100);
        polyline.createEquidistant(3);

        Point a = new Point(0, 0);
        Point b = new Point(50f, 50f);
        Point c = new Point(100f, 100f);

        assertEquals(polyline.getPoint(0), a);
        assertEquals(polyline.getPoint(1), b);
        assertEquals(polyline.getPoint(2), c);
    }

    @Test
    public void multiLine() {
        Polyline polyline = new Polyline();
        polyline.addPoint(0, 0);
        polyline.addPoint(0, 40);
        polyline.addPoint(60, 40);
        polyline.createEquidistant(4);

        Point a = new Point(0, 0);
        Point b = new Point(0, 100 / 3f);
        Point c = new Point(60 - 100 / 3f, 40);
        Point d = new Point(60, 40);

        assertEquals(polyline.getPoint(0), a);
        assertEquals(polyline.getPoint(1), b);
        assertEquals(polyline.getPoint(2), c);
        assertEquals(polyline.getPoint(3), d);
    }
}
