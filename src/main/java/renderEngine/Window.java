package renderEngine;

import Misc.WorldConstants;
import imgui.ImGui;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import static org.lwjgl.glfw.GLFW.*;

public class Window {
    private static long window;
    private final int WIDTH, HEIGHT;
    private final String TITLE;
    private final ImGuiImplGlfw imGui = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

    public Window(int width, int height, String title) {
        this.WIDTH = width;
        this.HEIGHT = height;
        this.TITLE = title;
    }

    public void createWindow() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");
        GLFW.glfwDefaultWindowHints();
        glfwWindowHint(GLFW_SAMPLES, 8);
        glfwWindowHint(GLFW_DEPTH_BITS, 24);
        window = glfwCreateWindow(WIDTH, HEIGHT, TITLE, WorldConstants.FULLSCREEN ? glfwGetPrimaryMonitor() : 0, 0);
        if (window == 0) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(
                window,
                (vidmode.width() - WIDTH) / 2,
                (vidmode.height() - HEIGHT) / 2);
        glfwMakeContextCurrent(window);
        glfwShowWindow(window);
        glfwSetCursorPos(window, WIDTH / 2f, HEIGHT / 2f);
        GL.createCapabilities();
        GL30.glEnable(GL11.GL_DEPTH_TEST);
        GL30.glEnable(GL30.GL_CULL_FACE);
        // glfwSwapInterval(1);
        GL30.glEnable(GL30.GL_CLIP_DISTANCE0);

        // GL30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        // GL30.glPolygonMode(GL30.GL_FRONT_AND_BACK,GL30.GL_LINE);

        ImGui.createContext();
        this.imGui.init(window, true);
        this.imGuiGl3.init("#version 130");
    }

    public ImGuiImplGlfw imGui() {
        return this.imGui;
    }

    public ImGuiImplGl3 imGuiGl3() {
        return this.imGuiGl3;
    }

    public static boolean shouldClose() {
        return glfwWindowShouldClose(window);
    }

    public void swapBuffers() {
        glfwSwapBuffers(window);
    }

    public void update() {
        glfwPollEvents();
    }

    public static void hideCursor(boolean hide) {
        int code = hide ? GLFW_CURSOR_DISABLED : GLFW_CURSOR_NORMAL;
        glfwSetInputMode(window, GLFW_CURSOR, code);
    }

    public static boolean isKeyPressed(int keyCode) {
        return glfwGetKey(window, keyCode) == GLFW_PRESS;
    }

    public int getWidth() {
        return WIDTH;
    }

    public int getHeight() {
        return HEIGHT;
    }

    public String getTitle() {
        return TITLE;
    }

    public long getWindow() {
        return window;
    }
}
