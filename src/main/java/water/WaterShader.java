package water;

import shaders.ShaderProgram;

public class WaterShader extends ShaderProgram {
    public WaterShader() throws Exception {
        super("water/waterVertex.txt", "water/waterFragment.txt");
        createUniform("projectionMatrix");
        createUniform("viewMatrix");
        createUniform("transformationMatrix");
        createUniform("lightPosition");
        createUniform("lightColour");
        createUniform("skyColour");
        createUniform("plane");
        createUniform("time");
        createUniform("dudvMap");
        createUniform("cameraPos");
        createUniform("reflectionTexture");
        createUniform("normalMap");
        createUniform("isEyeInWater");
        createUniform("refractionTexture");
        createUniform("waveStrength");
        createUniform("depthMap");
    }
    public void connectTextureUnits(){
        setUniform("reflectionTexture", 1);
        setUniform("refractionTexture", 2);
        setUniform("dudvMap", 3);
        setUniform("normalMap", 4);
        setUniform("depthMap", 5);
    }
}
