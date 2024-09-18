package water;

import Misc.DisplayTime;
import Misc.FrustumCullingFilter;
import Misc.WorldConstants;
import Textures.Texture;

import models.Entity;
import models.Light;
import models.Mesh;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL30;
import postProcessing.Fbo;
import renderEngine.Camera;
import renderEngine.Loader;
import renderEngine.Renderer;
import renderEngine.Transformation;
import terrain.TerrainChunkManager;
import terrain.TerrainMeshBuilder;


public class WaterRenderer extends Renderer {
    private WaterShader waterShader;
    private Fbo reflectionFbo;
    private Fbo refractionFbo;
    private int dudvTexture;
    private int normalMap;
    private FrustumCullingFilter frustumCullingFilter;
    private Mesh waterMesh;
    Light light = new Light(new Vector3f(-50000, 80, 0), new Vector3f(1,1,1));
    public WaterRenderer(Camera camera,
                         FrustumCullingFilter filter,
                         Fbo reflectionFbo,
                         Fbo refractionFbo,
                         Loader loader) throws Exception{
        super(camera);
        this.frustumCullingFilter = filter;
        this.waterShader = new WaterShader();
        this.reflectionFbo = reflectionFbo;
        this.refractionFbo = refractionFbo;
        dudvTexture = new Texture(WorldConstants.RES_DIR + "water/waterDUDV.png").getID();
        normalMap = new Texture(WorldConstants.RES_DIR + "water/normalMap.png").getID();

    }

    public void render(Vector4f planeClip, boolean setCulling){
        waterShader.start();
        Matrix4d viewMatrix = Transformation.getViewMatrix(camera);
        Matrix4f projectionMatrix = Transformation.getProjectionMatrix();
        GL30.glActiveTexture(GL30.GL_TEXTURE1);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, reflectionFbo.getColourTexture());
        GL30.glActiveTexture(GL30.GL_TEXTURE2);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, refractionFbo.getColourTexture());
        GL30.glActiveTexture(GL30.GL_TEXTURE3);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, dudvTexture);
        GL30.glActiveTexture(GL30.GL_TEXTURE4);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, normalMap);
        GL30.glActiveTexture(GL30.GL_TEXTURE5);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, refractionFbo.getDepthTexture());


        waterShader.connectTextureUnits();
        waterShader.setUniform(light);
        waterShader.setUniform("plane", planeClip);
        waterShader.setUniform("skyColour", WorldConstants.SKYCOLOUR);
        waterShader.setUniform("projectionMatrix", projectionMatrix);
        waterShader.setUniform("isEyeInWater", camera.isInWater());
        waterShader.setUniform("viewMatrix", viewMatrix);
        waterShader.setUniform("cameraPos", camera.getPosition());
        waterShader.setUniform("time", DisplayTime.getWaveTime());

//        //GL30.glEnable(GL30.GL_BLEND);
        if(camera.isInWater()) {
            GL30.glDisable(GL30.GL_CULL_FACE);
        }
        else {
            GL30.glEnable(GL30.GL_BLEND);
            GL30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        }

        Entity chunkEntity = new Entity();
        chunkEntity.setPosition(-32000, TerrainChunkManager.RADIUS, -32000);
        Matrix4f modelViewMatrix = Transformation.getTransformation(chunkEntity);
        waterShader.setUniform("waveStrength", 0.05f);
        waterShader.setUniform("transformationMatrix", modelViewMatrix);
        waterMesh.render();
        if(camera.isInWater() && setCulling) {
            GL30.glEnable(GL30.GL_CULL_FACE);
        }

        GL30.glDisable(GL30.GL_BLEND);
        waterShader.unbind();
    }
}
