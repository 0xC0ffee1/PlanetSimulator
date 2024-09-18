package renderEngine;


import Misc.WorldConstants;
import org.joml.*;
import org.lwjgl.opengl.GL30;

import java.lang.Math;

import static org.lwjgl.glfw.GLFW.*;

public class Camera {
    private final Vector3d position;

    private final Vector3f rotation;

    private boolean locked = false;

    private final Vector3f velocity;

    Window window;

    private Vector3d lookAt;

    public Camera(Window window){
        this.window = window;
        position = new Vector3d(0, 0, 0);
        rotation = new Vector3f(0, 0, 0);
        velocity = new Vector3f();
    }
    public Camera(Window window, Vector3d position, Vector3f rotation) {
        this.window = window;
        this.position = position;
        this.rotation = rotation;
        velocity = new Vector3f();
    }

    public boolean isInWater(){
        return position.y < WorldConstants.WATER_LEVEL;
    }

    public Vector3d getPosition() {
        return position;
    }

    public void setPosition(double x, double y, double z) {
        position.x = x;
        position.y = y;
        position.z = z;
    }

    public Vector3f getVelocity() {
        return velocity;
    }

    public void resetVelocity(){
        velocity.mul(0);
    }

    public void movePosition(float offsetX, float offsetY, float offsetZ) {
        if ( offsetZ != 0 ) {
            float x = (float)Math.sin(Math.toRadians(getYaw())) * -1.0f * offsetZ;
            float z = (float)Math.cos(Math.toRadians(getYaw())) * offsetZ;
            position.x += x;
            position.z += z;
        }
        if ( offsetX != 0) {
            float x = (float)Math.sin(Math.toRadians(getYaw() - 90)) * -1.0f * offsetX;
            float z = (float)Math.cos(Math.toRadians(getYaw() - 90)) * offsetX;
            position.x += x;
            position.z += z;
        }
        position.y += offsetY;
    }


    public Vector3f getRotation() {
        return rotation;
    }

    public void lock(){
        if(locked){
            locked = false;
            glfwSetInputMode(window.getWindow(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        }
        else{
            locked = true;
            glfwSetInputMode(window.getWindow(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        }
    }

    public boolean getLocked(){
        return locked;
    }

    public void setRotation(float x, float y, float z) {
        rotation.x = x;
        rotation.y = y;
        rotation.z = z;
    }

    public void invertPitch(){
        rotation.x = -rotation.x;
    }

    public double getPitch(){
      //  if(hasTarget()) return (position.x - target.x) * Math.PI/180d;
        return rotation.x;
    }

    public double getYaw(){
      //  if(hasTarget()) return (position.y - target.y) * Math.PI/180d;
        return rotation.y;
    }

    public void moveRotation(float offsetX, float offsetY, float offsetZ) {
        rotation.x += offsetX;
        rotation.y += offsetY;
        rotation.z += offsetZ;
    }


    public Vector3d getLookAt() {
        return lookAt;
    }

    public void lookAt(Vector3d target){
        this.lookAt = target;
    }

    public Vector3f getSightVector(){
        double rotX = getPitch()/180*Math.PI;
        double rotY = getYaw()/180*Math.PI;

        double dx = Math.sin(rotY);
        double dz = -Math.cos(rotY);
        double dy = -Math.sin(rotX);
        double m = Math.cos(rotX);

        return new Vector3f((float) (dx*m), (float) dy, (float) (dz*m));
    }

}
