package postProcessing;

import models.Planet;
import org.joml.Vector3f;
import renderEngine.Camera;
import renderEngine.Transformation;
import shaders.ShaderProgram;

public class OceanShader extends ShaderProgram {
    public OceanShader() throws Exception {
        super("postprocessing/bloom/simpleVertex.txt", "postprocessing/ocean/oceanFragment.glsl");
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
    }

    public void setUniforms(Camera camera, Planet planet){
        setUniform("cameraViewDir", camera.getSightVector());
        setUniform("worldSpaceCameraPos", camera.getPosition());
        setUniform("planetRadius", (float) planet.getRadius());
        setUniform("planetCentre", planet.getWorldSpacePos());
        setUniform("atmosphereRadius", planet.getRadius() * 1.01f);
        setUniform("inverseProjection", Transformation.getProjectionMatrix().invert());
        setUniform("inverseView", Transformation.getViewMatrix(camera).invert());
        setUniform("dirToSun", new Vector3f(-1, 2, 0f));
    }
}
