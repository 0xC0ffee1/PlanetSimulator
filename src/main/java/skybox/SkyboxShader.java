package skybox;

import org.joml.*;
import renderEngine.Camera;
import renderEngine.Transformation;
import shaders.ShaderProgram;

public class SkyboxShader extends ShaderProgram{

    private static final String VERTEX_FILE = "skybox/skyboxVertex.txt";
    private static final String FRAGMENT_FILE = "skybox/skyboxFragment.txt";
    private float rotation = 0;
    private float time = 0;
    private final float ROTATION_SPEED = 0.0001f;

    public SkyboxShader() throws Exception {
        super(VERTEX_FILE, FRAGMENT_FILE);
        super.createUniform("projectionMatrix");
        super.createUniform("viewMatrix");
        super.createUniform("skyColour");
    }

    public void loadProjectionMatrix(Matrix4f matrix){
        super.setUniform("projectionMatrix", matrix);
    }

    public void updateRotation(){
        rotation += ROTATION_SPEED;
    }

    public void loadViewMatrix(Camera camera){
        Matrix4d matrix = Transformation.getViewMatrix(camera);
        matrix.setColumn(3, new Vector4d(0,0,0,1));
        matrix.rotateY(rotation);

        super.setUniform("viewMatrix", matrix);
    }

}
