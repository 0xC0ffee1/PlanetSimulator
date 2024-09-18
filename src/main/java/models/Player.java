package models;

import Input.MouseInput;
import Misc.*;
import org.joml.Vector2f;
import org.joml.Vector3f;
import renderEngine.Camera;
import renderEngine.Window;
import terrain.TerrainMeshBuilder;

import static org.lwjgl.glfw.GLFW.*;

public class Player {
    private final Camera camera;
    private final Vector3f vel;

    private final Window window;
    private final MouseInput mouseInput;
    private static float GRAVITY = 0.04f;
    private static final float CAMERA_POS_STEP = 0.05f;
    private static final float MOUSE_SENSITIVITY = 0.2f;
    private  float cameraSpeedInc = 0.09f;
    private float maxCameraSpeed = 1.5f;
    private float jumpForce = 1.5f;
    private boolean inAir = true;
    private boolean isInWater = false;
    private boolean lastFrameInWater = false;
    private boolean jumped = false;
    private float jumpRadians = jumpForce;
    private boolean noClip = true;

    public static Vector3f atmosTesting = new Vector3f(5.5e-6f, 13.0e-6f, 22.4e-6f);

    public Player(Camera camera, MouseInput mouseInput, Window window) {
        this.camera = camera;
        this.mouseInput = mouseInput;
        vel = new Vector3f();

        this.window = window;
    }


    public void addForce(Vector3f force){
//        if(Math.abs(vel.x+force.x) > maxCameraSpeed) return;
//        if(Math.abs(vel.y+force.y) > 6 && !noClip || Math.abs(vel.y+force.y) > maxCameraSpeed && noClip)  return;
//        if(Math.abs(vel.z+force.z) > maxCameraSpeed) return;
        vel.add(force);
    }

    public void input() {
        mouseInput.input(window);

        if(WorldConstants.I_MOVE){
            addForce(new Vector3f(0,0,-cameraSpeedInc / 3));
        }

        if(Window.isKeyPressed(GLFW_KEY_F1)){
            atmosTesting.add(1e-8f, 0, 0);
        }
        if(Window.isKeyPressed(GLFW_KEY_F2)){
            atmosTesting.add(0, 1e-8f, 0);
        }
        if(Window.isKeyPressed(GLFW_KEY_F3)){
            atmosTesting.add(0, 0, 1e-8f);
        }

        if (Window.isKeyPressed(GLFW_KEY_W)) {
            addForce(new Vector3f(0, 0,-cameraSpeedInc));
        } else if (Window.isKeyPressed(GLFW_KEY_S)) {
            addForce(new Vector3f(0, 0,cameraSpeedInc));
        }

        if (Window.isKeyPressed(GLFW_KEY_A)) {
            addForce(new Vector3f(-cameraSpeedInc, 0,0));

        } else if (Window.isKeyPressed(GLFW_KEY_D)) {
            addForce(new Vector3f(cameraSpeedInc, 0,0));
        }

        if (Window.isKeyPressed(GLFW_KEY_LEFT_CONTROL)) {
            cameraSpeedInc = 50;
        }
        else if(Window.isKeyPressed(GLFW_KEY_RIGHT_CONTROL)){
            cameraSpeedInc = 5000;
        }
        else {
            cameraSpeedInc = 1;
        }
        if (Window.isKeyPressed(GLFW_KEY_C)) {
            vel.mul(0, 0, 0.99f);
        }

        if(Window.isKeyPressed(GLFW_KEY_N)){
            noClip = !noClip;
        }

        if (Window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            addForce(new Vector3f(0,-cameraSpeedInc, 0));
        } else if (Window.isKeyPressed(GLFW_KEY_SPACE) && !inAir) {
            if(!noClip) {
                addForce(new Vector3f(0, jumpForce,0));
                inAir = true;
            }
            else {
                addForce(new Vector3f(0, cameraSpeedInc, 0));
            }

        }

        if(Window.isKeyPressed(GLFW_KEY_SPACE) && noClip){
            addForce(new Vector3f(0, cameraSpeedInc, 0));
        }

        if(Window.isKeyPressed(GLFW_KEY_Z)){
            WorldConstants.FOV = (float) Math.toRadians(20.0f);
        }
        else {
            WorldConstants.FOV = (float) Math.toRadians(60.0f);
        }
        if(Window.isKeyPressed(GLFW_KEY_F5)){
            WorldConstants.NO_GUI = !WorldConstants.NO_GUI;
        }

        //collision(new Vector3f(cameraInc.x,cameraInc.y,cameraInc.z));
        if (Window.isKeyPressed(GLFW_KEY_ESCAPE) && mouseInput.isLeftButtonClicked()) {
            camera.lock();
        }
        mouseInput.resetScroll();
    }

    public void updateCamera() {
        //+ (camera.getRotation().x * cameraInc.z/1000
        if (Math.abs(vel.x) < 0.02) vel.x = 0;
        if (Math.abs(vel.y) < 0.02) vel.y = 0;
        if (Math.abs(vel.z) < 0.02) vel.z = 0;

        if(new Vector3f(vel).mul(1,0,1).length() > 0 && !noClip){
            Vector3f drag = new Vector3f(vel.x,0, vel.z);
            drag.normalize();
            float c = -0.05f;
            drag.mul(c);
            addForce(drag);
        }
        else if(vel.length() > 0 && noClip){
            Vector3f drag = new Vector3f(vel);
            drag.normalize();
            float c = -0.05f;
            drag.mul(c);
            addForce(drag);
        }

        camera.movePosition(vel.x * CAMERA_POS_STEP,
                vel.y * CAMERA_POS_STEP+ (camera.getRotation().x * vel.z/1000),
                vel.z * CAMERA_POS_STEP);

        // Update camera based on mouse
        if (!camera.getLocked()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
        }

    }

        public void update(){
            if(!noClip) addForce(new Vector3f(0, -GRAVITY, 0));
            updateCamera();
            input();
        }
}
