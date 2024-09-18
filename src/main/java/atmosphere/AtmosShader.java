package atmosphere;

import Misc.DisplayTime;
import models.Entity;
import models.Planet;
import models.Player;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import renderEngine.Camera;
import renderEngine.Transformation;
import shaders.ShaderProgram;
import terrain.TerrainChunkManager;
import terrain.TerrainRenderer;


public class AtmosShader extends ShaderProgram {
    public static float[] sliderValue = new float[1];
    public static int[] scatterPoints = new int[1];
    public static int[] opticalPoints = new int[1];

    public static int[] magicValue = new int[1];
    public static float[] intensity = new float[1];
    public static float[] densityFalloff = new float[1];

    public static float[] waveLengths = new float[3];



    public AtmosShader() throws Exception {
        super("postprocessing/bloom/simpleVertex.txt", "postprocessing/atmosphere/atmosFragment.glsl");
        createUniform("cameraViewDir");
        createUniform("planetCentre");
        createUniform("atmosphereRadius");
        createUniform("planetRadius");
        createUniform("worldSpaceCameraPos");
        createUniform("inverseProjection");
        createUniform("inverseView");
        createUniform("dirToSun");
        createUniform("colourTexture");
        createUniform("depthTexture");
        createUniform("numInScatteringPoints");
        createUniform("numOpticalDepthPoints");
        createUniform("magicValue");
        createUniform("intensity");
        createUniform("densityFalloff");
        createUniform("scatter");
        //createUniform("weatherClarity");
        //createUniform("betaR");

        sliderValue[0] = 1.2f;
        scatterPoints[0] = 19;
        opticalPoints[0] = 26;
        magicValue[0] = 663000;
        intensity[0] = 1;
        densityFalloff[0] = 4.3f;

        waveLengths[0] = 770;
        waveLengths[1] = 530;
        waveLengths[2] = 440;
    }

    public void setUniforms(Camera camera, Planet planet){
        //fix magic number
        int radius = planet.getRadius() - magicValue[0];

        setUniform("cameraViewDir", camera.getSightVector());
        setUniform("numInScatteringPoints", scatterPoints[0]);
        setUniform("numOpticalDepthPoints", opticalPoints[0]);
        setUniform("magicValue", magicValue[0]);
        setUniform("intensity", intensity[0]);
        setUniform("densityFalloff", densityFalloff[0]);
        setUniform("scatter", new Vector3f(waveLengths));

        setUniform("worldSpaceCameraPos", camera.getPosition());
        setUniform("planetRadius", (float) radius);
        //setUniform("betaR", Player.atmosTesting);
        setUniform("planetCentre", planet.getWorldSpacePos());
        setUniform("atmosphereRadius", radius * sliderValue[0]);
        setUniform("inverseProjection", Transformation.getProjectionMatrix().invert());
        setUniform("inverseView", Transformation.getViewMatrix(camera).invert());
        setUniform("dirToSun", new Vector3f(TerrainRenderer.light.getPosition()).sub(TerrainChunkManager.EARTH.getWorldSpacePos()).normalize());
        //setUniform("weatherClarity", 0);
    }
}
