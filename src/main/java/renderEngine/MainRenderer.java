package renderEngine;

import Gui.GuiRenderer;
import Misc.FrustumCullingFilter;
import Misc.WorldConstants;
import atmosphere.AtmosFilter;
import atmosphere.AtmosShader;
import imgui.ImDrawData;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import models.Planet;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL11;
import particles.ParticleHandler;
import skybox.SkyboxRenderer;
import TextRendering.TextMaster;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL30;
import postProcessing.Fbo;
import postProcessing.PostProcessing;
import terrain.TerrainChunkManager;
import terrain.TerrainRenderer;
import water.UnderWaterRenderer;
import water.WaterRenderer;

public class MainRenderer extends Renderer {
    private SkyboxRenderer skyboxRenderer;
    private Fbo postProcessedScreen;
    private Fbo excludePostProcessing;
    private Fbo reflectionFbo;
    private Fbo refractionFbo;
    private Window window;
    private final GuiRenderer guiRenderer;
    private FrustumCullingFilter frustumCullingFilter;
    private TerrainRenderer terrainRenderer;
    private UnderWaterRenderer underWaterRenderer;
    private WaterRenderer waterRenderer;

    private TerrainChunkManager terrainChunkManager;

    public MainRenderer(Camera camera,
            Window window,
            TerrainChunkManager terrainChunkManager,
            SkyboxRenderer skyboxRenderer,
            GuiRenderer guiRenderer,
            Loader loader) throws Exception {
        super(camera);
        this.terrainChunkManager = terrainChunkManager;
        frustumCullingFilter = new FrustumCullingFilter();
        this.window = window;
        this.skyboxRenderer = skyboxRenderer;
        this.reflectionFbo = new Fbo(WorldConstants.WINDOW_WIDTH, WorldConstants.WINDOW_HEIGHT, Fbo.DEPTH_TEXTURE,
                window);
        this.refractionFbo = new Fbo(1280, 720, Fbo.DEPTH_TEXTURE, window);
        this.postProcessedScreen = new Fbo(window.getWidth(), window.getHeight(), Fbo.DEPTH_TEXTURE, window);
        this.excludePostProcessing = new Fbo(window.getWidth(), window.getHeight(), Fbo.DEPTH_TEXTURE, window);
        this.guiRenderer = guiRenderer;
        waterRenderer = new WaterRenderer(camera, frustumCullingFilter, reflectionFbo, refractionFbo, loader);
        underWaterRenderer = new UnderWaterRenderer(loader, postProcessedScreen, reflectionFbo);
        this.terrainRenderer = new TerrainRenderer(camera, frustumCullingFilter);
    }

    public void updateFbos() {
        // Changing camera pos screws up terrain gen, fix
        // if(!camera.isInWater()){
        // reflectionFbo.bindFrameBuffer();
        // float distance = 2 * (camera.getPosition().y - WorldConstants.WATER_LEVEL);
        // camera.getPosition().y -= distance;
        // camera.invertPitch();
        // renderScene(new Vector4f(0,1,0, -WorldConstants.WATER_LEVEL-0.1f), false,
        // false);
        // camera.getPosition().y += distance;
        // camera.invertPitch();
        // reflectionFbo.unbindFrameBuffer();
        // refractionFbo.bindFrameBuffer();
        // renderScene(new Vector4f(0,-1,0, WorldConstants.WATER_LEVEL-0.01f), false,
        // false);
        // refractionFbo.unbindFrameBuffer();
        // }
        // else {
        // reflectionFbo.bindFrameBuffer();
        // renderScene(new Vector4f(0,-1,0, WorldConstants.WATER_LEVEL+1f), false,
        // false);
        // reflectionFbo.unbindFrameBuffer();
        // refractionFbo.bindFrameBuffer();
        // renderScene(new Vector4f(0,1,0, -WorldConstants.WATER_LEVEL-0.01f), false,
        // false);
        // refractionFbo.unbindFrameBuffer();
        // }
    }

    public void renderMenuScreen() {
        window.swapBuffers();



        guiRenderer.render();
        TextMaster.render();



    }

    public void render() {
        window.swapBuffers();
        // updateFbos();
        clear();

        postProcessedScreen.bindFrameBuffer();
        renderScene(new Vector4f(0, 0, 0, 0));
        postProcessedScreen.unbindFrameBuffer();

        PostProcessing.doPostProcessing(postProcessedScreen.getColourTexture(), postProcessedScreen.getDepthTexture(),
                terrainChunkManager.getPlanets());

        GL30.glColorMask(false, false, false, false);
        renderScene(new Vector4f(0, 0, 0, 0)); // Render the depth information
        GL30.glColorMask(true, true, true, true);
        // Render all the things that need rendering with depth information and without
        // post processing
        ParticleHandler.renderParticles(camera);

        // if(camera.isInWater()){
        // underWaterRenderer.render();
        // }

        if (!WorldConstants.NO_GUI) {
            TextMaster.render();
            guiRenderer.render();
        }

        window.imGui().newFrame();

        ImGui.newFrame();

        if (ImGui.begin("Hello")) {
            ImGui.sliderInt("Magic Value", AtmosShader.magicValue, 600000, 750000);
            ImGui.sliderFloat("Atmosphere Radius", AtmosShader.sliderValue, 1f, 1.5f);
            ImGui.sliderFloat("Density Falloff", AtmosShader.densityFalloff, 0.1f, 10f);
            ImGui.sliderFloat("Atmosphere Intensity", AtmosShader.intensity, 1, 10);
            ImGui.sliderInt("Scatter Points", AtmosShader.scatterPoints, 1, 128);
            ImGui.sliderInt("Optical Points", AtmosShader.opticalPoints, 1, 128);
            ImGui.sliderFloat3("WaveLengths", AtmosShader.waveLengths , 1f, 1000f);
        }

        ImGui.end();

        ImGui.render();
        window.imGuiGl3().renderDrawData(ImGui.getDrawData());


    }

    public void clear() {
        GL30.glClearColor(0, 0, 0, 1);
        GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
    }

    public void renderScene(Vector4f planeClip) {
        clear();

        skyboxRenderer.render(camera);

        terrainRenderer.render(terrainChunkManager.getRenderList(), planeClip);
        // waterRenderer.render(planeClip,setCulling);
        //
        // atmosRenderer.render();
        //
    }

}
