package Input;

import Misc.WorldConstants;
import org.joml.Vector2d;
import org.joml.Vector2f;
import renderEngine.Window;

import static org.lwjgl.glfw.GLFW.*;

public class MouseInput {

    private final Vector2d previousPos;

    private final Vector2d currentPos;

    private final Vector2f displVec;

    private boolean inWindow = false;

    private boolean leftButtonPressed = false;

    private boolean rightButtonPressed = false;

    private boolean rightButtonReleased = false;
    private boolean leftButtonReleased = false;

    public double scrollX = 0;
    public double scrollY = 0;

    public MouseInput() {
        previousPos = new Vector2d(-1, -1);
        currentPos = new Vector2d(0, 0);
        displVec = new Vector2f();
    }

    public void resetScroll(){
        scrollY = 0;
        scrollX = 0;
    }

    public void init(Window window) {
        glfwSetCursorPosCallback(window.getWindow(), (windowHandle, xpos, ypos) -> {
            currentPos.x = xpos;
            currentPos.y = ypos;
        });
        glfwSetCursorEnterCallback(window.getWindow(), (windowHandle, entered) -> {
            inWindow = entered;
        });
        glfwSetMouseButtonCallback(window.getWindow(), (windowHandle, button, action, mode) -> {
            leftButtonPressed = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS;
            rightButtonPressed = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS;
        });

        glfwSetScrollCallback(window.getWindow(), (windowHandle, xoffset, yoffset) -> {
            scrollX = xoffset;
            scrollY = yoffset;
        });

    }

    public Vector2d getCurrentPos() {
        return currentPos;
    }

    public Vector2f getDisplVec() {
        return displVec;
    }

    public void input(Window window) {
        displVec.x = 0;
        displVec.y = 0;
        if (previousPos.x != 0 && previousPos.y != 0 && inWindow) {
            double deltax = currentPos.x - previousPos.x;
            double deltay = currentPos.y - previousPos.y;
            boolean rotateX = deltax != 0;
            boolean rotateY = deltay != 0;
            if (rotateX) {
                displVec.y = (float) deltax * WorldConstants.MOUSE_SENSITIVITY;
            }
            if (rotateY) {
                displVec.x = (float) deltay * WorldConstants.MOUSE_SENSITIVITY;
            }
        }
        previousPos.x = currentPos.x;
        previousPos.y = currentPos.y;
    }

    public boolean isLeftButtonPressed() {
        return leftButtonPressed;
    }

    public boolean isLeftButtonClicked(){
        if(isLeftButtonPressed()){
            leftButtonReleased = true;
            return false;
        }
        else{
            if(leftButtonReleased){
                leftButtonReleased = false;
                return true;
            }
            return false;
        }
    }

    public boolean isRightButtonClicked(){
        if(isRightButtonPressed()){
            rightButtonReleased = true;
            return false;
        }
        else{
            if(rightButtonReleased){
                rightButtonReleased = false;
                return true;
            }
            return false;
        }
    }

    public boolean isRightButtonPressed(){
        return rightButtonPressed;
    }
}