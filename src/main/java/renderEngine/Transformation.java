package renderEngine;


import Misc.WorldConstants;
import models.Entity;
import org.joml.*;

import java.lang.Math;

public class Transformation {
    public static Matrix4f projectionMatrix = new Matrix4f();
    public static Matrix4f getProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
        projectionMatrix.identity();
        float aspectRatio = width / height;
        projectionMatrix.perspective(fov, aspectRatio, zNear, zFar);
        return projectionMatrix;
    }

    public static Matrix4f getProjectionMatrix() {
        projectionMatrix.identity();
        float aspectRatio = (float) WorldConstants.WINDOW_WIDTH / WorldConstants.WINDOW_HEIGHT;
        projectionMatrix.perspective(WorldConstants.FOV, aspectRatio, WorldConstants.Z_NEAR, WorldConstants.Z_FAR);
        return projectionMatrix;
    }

    public static Matrix4f getModelViewMatrix(Entity gameItem, Matrix4f viewMatrix) {

        Matrix4f modelViewMatrix = new Matrix4f();
        Vector3f rotation = gameItem.getRotation();
        modelViewMatrix.identity().translate(gameItem.getPosition()).
                rotateX((float)Math.toRadians(-rotation.x)).
                rotateY((float)Math.toRadians(-rotation.y)).
                rotateZ((float)Math.toRadians(-rotation.z)).
                scale(gameItem.getScale());
        Matrix4f viewCurr = new Matrix4f(viewMatrix);
        return viewCurr.mul(modelViewMatrix);
    }

    public static Matrix4f getTransformation(Entity gameItem){
        Matrix4f transformation = new Matrix4f();
        Vector3f rotation = gameItem.getRotation();
        transformation.identity().translate(gameItem.getPosition()).
                rotateX((float)Math.toRadians(-rotation.x)).
                rotateY((float)Math.toRadians(-rotation.y)).
                rotateZ((float)Math.toRadians(-rotation.z)).
                scale(gameItem.getScale());


        return transformation;
    }

    public static Matrix4f getTransformation(Vector2f translation, Vector2f scale){
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.identity();
        matrix4f.translate(new Vector3f(translation.x, translation.y, 0), matrix4f);
        matrix4f.scale(new Vector3f(scale.x,scale.y,1f), matrix4f);
        return matrix4f;
    }

    public static Matrix4d getViewMatrix(Camera camera) {
        Matrix4d viewMatrix = new Matrix4d();
        Vector3d cameraPos = camera.getPosition();
        Vector3f rotation = camera.getRotation();

        viewMatrix.identity();
        //viewMatrix.setLookAt(cameraPos, new Vector3d(1,1,1), new Vector3d(0,1,0));
        // First do the rotation so camera rotates over its position
        viewMatrix.rotate((float)Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
                .rotate((float)Math.toRadians(rotation.y), new Vector3f(0, 1, 0));
        // Then do the translation
        viewMatrix.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        return viewMatrix;
    }
}
