import Gui.GuiListener;
import Gui.GuiRenderer;
import Gui.startmenu.PauseMenuManager;
import Gui.startmenu.StartMenu;
import Input.MouseInput;
import Misc.DisplayTime;
import Misc.Maths;
import Misc.WorldConstants;
import TextMesh.FontType;
import TextRendering.TextMaster;
import Textures.ParticleTexture;
import Textures.Texture;
import imgui.ImFont;
import imgui.ImFontConfig;
import imgui.ImFontGlyphRangesBuilder;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import models.Player;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.liquidengine.legui.system.renderer.Renderer;
import org.liquidengine.legui.system.renderer.nvg.NvgRenderer;
import particles.Particle;
import particles.ParticleHandler;
import particles.ParticleShader;
import particles.ParticleSystem;
import skybox.SkyboxRenderer;
import TextMesh.GUIText;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import postProcessing.PostProcessing;
import renderEngine.Camera;
import renderEngine.Loader;
import renderEngine.MainRenderer;
import renderEngine.Window;
import terrain.TerrainChunk;
import terrain.TerrainChunkManager;
import terrain.TerrainMeshBuilder;
import terrain.TerrainThreadManager;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    private Loader loader;
    private Window window;
    private GuiRenderer guiRenderer;
    private GuiListener guiListener;

    private MouseInput mouseInput;
    private Camera camera;
    private Player player;

    private Renderer leguiRenderer;

    private MainRenderer mainRenderer;

    public void run() throws Exception {
        window = new Window(WorldConstants.WINDOW_WIDTH, WorldConstants.WINDOW_HEIGHT, "Planets");
        window.createWindow();

        // leguiRenderer = new NvgRenderer();
        // leguiRenderer.initialize();
        //
        // GLFW.glfwMakeContextCurrent(window.getWindow());

        camera = new Camera(window);
        // camera.setPosition(2004405, 2559595, 2336923);
        camera.setPosition(1724665, 1441927, 3314605);
        mouseInput = new MouseInput();
        mouseInput.init(window);
        loader = new Loader();
        TerrainChunkManager terrainChunkManager = new TerrainChunkManager(camera);
        guiRenderer = new GuiRenderer(loader);
        guiListener = new GuiListener(mouseInput);
        mainRenderer = new MainRenderer(camera, window, terrainChunkManager, new SkyboxRenderer(loader, camera),
                guiRenderer, loader);
        FontType fontType = new FontType(new Texture(WorldConstants.RES_DIR + "fonts/voxelfont1.png").getID(),
                new File(WorldConstants.RES_DIR + "fonts/voxelfont.fnt"));
        TextMaster.init(loader);
        ParticleHandler.init(loader);

        player = new Player(camera, mouseInput, window);
        GUIText fps = new GUIText("", 1f, fontType, new Vector2f(0.01f, 0.02f), 0.2f, false);
        GUIText pos = new GUIText("", 1f, fontType, new Vector2f(0.01f, 0.06f), 0.5f, false);
        GUIText vel = new GUIText("", 1f, fontType, new Vector2f(0.01f, 0.1f), 1f, false);
        GUIText altitude = new GUIText("", 1f, fontType, new Vector2f(0.01f, 0.14f), 1f, false);
        GUIText timeDilation = new GUIText("", 1f, fontType, new Vector2f(0.01f, 0.18f), 1f, false);
        timeDilation.setColour(0.5f, 0, 0.5f);
        fps.setColour(0, 0, 1);
        pos.setColour(0, 0, 1);
        vel.setColour(0, 0, 1);
        altitude.setColour(0, 0, 1);

        ParticleTexture pT = new ParticleTexture(
                new Texture(WorldConstants.RES_DIR + "particles/snowflake.png").getID(), 1, true);
        ParticleTexture fogT = new ParticleTexture(new Texture(WorldConstants.RES_DIR + "particles/smoke.png").getID(),
                8, false);
        ParticleSystem particleSystem = new ParticleSystem(pT, 10000, 25, 2F, 5, 1);

        particleSystem.setDirection(new Vector3f(0.2f, -0.5f, 0.3f), 0.1f);
        particleSystem.setLifeError(0.1f);
        particleSystem.setSpeedError(0.4f);
        particleSystem.setScaleError(0.8f);
        particleSystem.setScattering(1000);

        ParticleSystem fogSystem = new ParticleSystem(fogT, 10000, 5, 0F, 5, 20);
        fogSystem.setScattering(1000);
        fogSystem.setScatteringY(200);
        fogSystem.setLifeError(0.4f);
        fogSystem.setSpeedError(0.7f);
        fogSystem.setScaleError(0.3f);
        fogSystem.setDirection(new Vector3f(1, 0, 0), 0.1f);

        long lastTime = System.nanoTime();
        double amountOfTicks = 144.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        PostProcessing.init(loader, window, camera);
        PauseMenuManager.setCurrentMenu(new StartMenu(guiRenderer));
        boolean initialized = false;
        Vector3d camPos = new Vector3d();
        boolean hide = false;

        while (!Window.shouldClose()) {

            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            double frameTime = (now - lastTime) / 1000000000f;
            lastTime = now;
            while (delta >= 1) {
                window.update();
                terrainChunkManager.update();
                if (Window.isKeyPressed(GLFW.GLFW_KEY_P)) {
                    PauseMenuManager.setCurrentMenu(new StartMenu(guiRenderer));
                }
                else  if (Window.isKeyPressed(GLFW.GLFW_KEY_L)) {
                    hide = !hide;
                    Window.hideCursor(hide);
                    camera.lock();
                }
                guiListener.listen();
                if (PauseMenuManager.canContinue()) {
                    update(frameTime);
                }
                delta--;

            }

            if (!Window.shouldClose()) {
                // renderEntities()
                if (PauseMenuManager.canContinue()) {
                    if (!initialized) {
                        terrainChunkManager.startTerrainGen();
                        initialized = true;
                    }

                    ParticleHandler.update(camera);
                    mainRenderer.render();
                } else {
                    mainRenderer.renderMenuScreen();
                }

            }
            frames++;

            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                if (!PauseMenuManager.canContinue())
                    continue;
                double d = camPos.distance(camera.getPosition());
                vel.resetString("Velocity: " + (Math.round((d) * 10)) / 10 + " m/s");
                fps.resetString("FPS: " + frames);
                double relativeTime = (1f / (Math.sqrt(1 - (d / (299792458f * 299792458f)))));
                timeDilation.resetString("Time Dilation: " + relativeTime);
                // altitude.resetString("Altitude: " +
                // (camera.getPosition().distance(TerrainChunkManager.EARTH.getWorldSpacePos())
                // - TerrainChunkManager.EARTH.getRadius()) + " m");
                pos.resetString(String.format("X: %f, Y: %f, Z: %f", camera.getPosition().x, camera.getPosition().y,
                        camera.getPosition().z));
                frames = 0;
                camPos.set(camera.getPosition());
            }
        }
        TextMaster.cleanUp();
        ParticleHandler.cleanUp();
    }

    public static void main(String[] args) {
        try {
            new Main().run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(double delta) {
        DisplayTime.refreshTime(delta);
        player.update();
    }
}
