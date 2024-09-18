package Misc;

import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class FrustumCullingFilter {

    private final Matrix4f prjViewMatrix;
    private int CHUNKSIZE = WorldConstants.CHUNKSIZE;

    private FrustumIntersection frustumInt;

    public FrustumCullingFilter() {
        prjViewMatrix = new Matrix4f();
        frustumInt = new FrustumIntersection();
    }

    public void updateFrustum(Matrix4f projMatrix, Matrix4f viewMatrix) {
        // Calculate projection view matrix
        prjViewMatrix.set(projMatrix);
        prjViewMatrix.mul(viewMatrix);
        // Update frustum intersection class
        frustumInt.set(prjViewMatrix);
    }

    public boolean testSphere(Vector3f center, int radius){
        return frustumInt.testSphere(center, radius);
    }

    public boolean testPoint(Vector3f center){
        return frustumInt.testPoint(center);
    }

    public boolean insideFrustum(float x0, float y0, float z0) {
        return frustumInt.testAab(x0, y0, z0, x0+CHUNKSIZE, y0+CHUNKSIZE, z0+CHUNKSIZE);
    }
}
