package terrain;

import shaders.ShaderProgram;

public class TerrainShader extends ShaderProgram {
    public TerrainShader() throws Exception {
        super("terrain/vertexShader.txt", "terrain/fragmentShader.txt");
        createUniform("viewMatrix");
        createUniform("transformationMatrix");
        createUniform("projectionMatrix");
        createUniform("lightPosition");
        createUniform("lightColour");
        createUniform("time");
        createUniform("skyColour");
        createUniform("texArray");
        createUniform("normalMapArray");
        createUniform("noiseMap");
        createUniform("planeClip");
        createUniform("cameraPos");
        createUniform("planetPos");
        createUniform("roughnessArray");
        createUniform("occlusionArray");
        createUniform("metalArray");
        createUniform("radius");
        createUniform("mainTexID");
    }
}
