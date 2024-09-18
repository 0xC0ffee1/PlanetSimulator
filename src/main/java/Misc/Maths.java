package Misc;

import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class Maths {
    public static float bilinearInterpolation(float bottomLeft, float topLeft, float bottomRight,
                                float topRight, float xMin, float xMax, float zMin,
                                float zMax, float x, float z) {
        float width = xMax - xMin, height = zMax - zMin,

                xDistanceToMaxValue = xMax - x, zDistanceToMaxValue = zMax - z,

                xDistanceToMinValue = x - xMin, zDistanceToMinValue = z - zMin;

        return 1.0f / (width * height) *
                (bottomLeft * xDistanceToMaxValue * zDistanceToMaxValue +
                        bottomRight * xDistanceToMinValue * zDistanceToMaxValue +
                        topLeft * xDistanceToMaxValue * zDistanceToMinValue +
                        topRight * xDistanceToMinValue * zDistanceToMinValue);
    }

    public static void applyMatrix4To3DVector(Vector3f vector, Matrix4f matrix){
        float x = vector.x, y = vector.y, z = vector.z;
        float[] e = new float[16];
        matrix.get(e);

        float w = 1 / ( e[ 3 ] * x + e[ 7 ] * y + e[ 11 ] * z + e[ 15 ] );

        vector.set(( e[ 0 ] * x + e[ 4 ] * y + e[ 8 ] * z + e[ 12 ] ) * w,
                ( e[ 1 ] * x + e[ 5 ] * y + e[ 9 ] * z + e[ 13 ] ) * w,
                ( e[ 2 ] * x + e[ 6 ] * y + e[ 10 ] * z + e[ 14 ] ) * w);

    }

    public static void applyMatrix4To3DVector(Vector3d vector, Matrix4f matrix){
        double x = vector.x, y = vector.y, z = vector.z;
        float[] e = new float[16];
        matrix.get(e);

        double w = 1 / ( e[ 3 ] * x + e[ 7 ] * y + e[ 11 ] * z + e[ 15 ] );

        vector.set(( e[ 0 ] * x + e[ 4 ] * y + e[ 8 ] * z + e[ 12 ] ) * w,
                ( e[ 1 ] * x + e[ 5 ] * y + e[ 9 ] * z + e[ 13 ] ) * w,
                ( e[ 2 ] * x + e[ 6 ] * y + e[ 10 ] * z + e[ 14 ] ) * w);

    }

    public static void transformDirection( Vector3f vector, Matrix4f matrix ) {

        // input: THREE.Matrix4 affine matrix
        // vector interpreted as a direction

        float x = vector.x, y = vector.y, z = vector.z;
        float[] e = new float[16];
        matrix.get(e);

        vector.x = e[ 0 ] * x + e[ 4 ] * y + e[ 8 ] * z;
        vector.y = e[ 1 ] * x + e[ 5 ] * y + e[ 9 ] * z;
        vector.z = e[ 2 ] * x + e[ 6 ] * y + e[ 10 ] * z;
        vector.normalize();

    }

    public static void transformDirection( Vector3d vector, Matrix4f matrix ) {

        // input: THREE.Matrix4 affine matrix
        // vector interpreted as a direction

        double x = vector.x, y = vector.y, z = vector.z;
        float[] e = new float[16];
        matrix.get(e);

        vector.x = e[ 0 ] * x + e[ 4 ] * y + e[ 8 ] * z;
        vector.y = e[ 1 ] * x + e[ 5 ] * y + e[ 9 ] * z;
        vector.z = e[ 2 ] * x + e[ 6 ] * y + e[ 10 ] * z;
        vector.normalize();

    }

    private static float smoothstep(float edge0, float edge1, float x)
    {
        // Scale, bias and saturate x to 0..1 range
        x = x * x * (3 - 2 * x);
        // Evaluate polynomial
        return (edge0 * x) + (edge1 * (1 - x));
    }

    public static float smoothInterpolation(float bottomLeft, float topLeft, float bottomRight,
                              float topRight, float xMin, float xMax, float zMin,
                              float zMax, float x, float z)
    {
        float width = xMax - xMin, height = zMax - zMin;
        float xValue = 1 - (x - xMin) / width;
        float zValue = 1 - (z - zMin) / height;

        float a = smoothstep(bottomLeft, bottomRight, xValue);
        float b = smoothstep(topLeft, topRight, xValue);
        return smoothstep(a, b, zValue);
    }

}
