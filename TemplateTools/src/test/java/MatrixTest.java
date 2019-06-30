import com.lh.component.matrix.MatrixUtils;
import org.ejml.simple.SimpleMatrix;
import org.junit.Assert;
import org.junit.Test;

public class MatrixTest {
    @Test
    public void testAngle() {
        double[] a = {2, 3};
        double[] b = {-1, 4};
        double angle = MatrixUtils.getAngleFromVectorAToB(a, b);
        SimpleMatrix vA = new SimpleMatrix(new float[][]{{2, 3}});
        SimpleMatrix rotateM = new SimpleMatrix(new double[][]{{Math.cos(angle), -Math.sin(angle)},
                {Math.abs(Math.sin(angle)), Math.abs(Math.cos(angle))}
        });
        SimpleMatrix vB = vA.mult(rotateM);
        double scaling = b[0] / vB.get(0);
        Assert.assertEquals(vB.get(0) * scaling, b[0], 0.00000001);
        Assert.assertEquals(vB.get(1) * scaling, b[1], 0.00000001);
    }
}
