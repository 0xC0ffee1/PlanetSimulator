package atmosphere;

import models.Planet;
import org.joml.Vector3f;
import renderEngine.Camera;
import renderEngine.Transformation;
import shaders.ShaderProgram;

public class AtmosShapeShader extends ShaderProgram {
    public AtmosShapeShader() throws Exception {
        super("atmosphere/atmosVertex.txt", "atmosphere/atmosFragment.txt");
        createUniform("view");
        createUniform("proj");
    }

    public void setUniforms(Camera camera, Planet planet){
        setUniform("view", Transformation.getViewMatrix(camera));
        setUniform("proj", Transformation.getProjectionMatrix());
    }
}
