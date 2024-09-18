package skybox;

import Misc.WorldConstants;
import atmosphere.AtmosShapeShader;
import models.Mesh;
import org.lwjgl.opengl.GL30;
import renderEngine.Camera;
import renderEngine.Loader;
import renderEngine.Renderer;
import renderEngine.Transformation;
import terrain.TerrainChunkManager;

public class SkyboxRenderer extends Renderer {
    private static final float SIZE = 400000f;

    private static final float[] VERTICES = {
            -SIZE,  SIZE, -SIZE,
            -SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,

            -SIZE, -SIZE,  SIZE,
            -SIZE, -SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE,  SIZE,
            -SIZE, -SIZE,  SIZE,

            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,

            -SIZE, -SIZE,  SIZE,
            -SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE, -SIZE,  SIZE,
            -SIZE, -SIZE,  SIZE,

            -SIZE,  SIZE, -SIZE,
            SIZE,  SIZE, -SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            -SIZE,  SIZE,  SIZE,
            -SIZE,  SIZE, -SIZE,

            -SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE,  SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE,  SIZE,
            SIZE, -SIZE,  SIZE};

    private static String[] TEXTURE_FILES = {"right", "left", "top", "bottom", "front", "back"};
    private Mesh cube;
    private int textureID;
    private SkyboxShader shader;

    public SkyboxRenderer(Loader loader, Camera camera) throws Exception{
        super(camera);
        //cube = loader.loadToVAO(positions, indices, textCoords);
        cube = loader.loadToVAO(VERTICES, 3);
        textureID = loader.loadCubeMap(TEXTURE_FILES);
        this.shader = new SkyboxShader();
        shader.start();
        shader.loadProjectionMatrix(Transformation.getProjectionMatrix());
        shader.unbind();
    }

    public void updateRotation(){
        shader.updateRotation();
    }



    public void render(Camera camera){
        shader.start();
        shader.setUniform("skyColour", WorldConstants.SKYCOLOUR);
        shader.loadViewMatrix(camera);
        shader.loadProjectionMatrix(Transformation.getProjectionMatrix());
        GL30.glBindVertexArray(cube.getVaoID());
        GL30.glActiveTexture(GL30.GL_TEXTURE0);
        GL30.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP, textureID);
        //cube.render();
        GL30.glDrawArrays(GL30.GL_TRIANGLES, 0,cube.getVertexCount());
        shader.unbind();
        GL30.glBindVertexArray(0);

    }


}
