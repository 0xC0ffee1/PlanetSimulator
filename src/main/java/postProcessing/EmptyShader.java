package postProcessing;

import shaders.ShaderProgram;

public class EmptyShader extends ShaderProgram {
    private static final String VERTEX_FILE = "postprocessing/bloom/simpleVertex.txt";
    private static final String FRAGMENT_FILE = "postprocessing/simpleFragment.txt";

    public EmptyShader() throws Exception {
        super(VERTEX_FILE, FRAGMENT_FILE);
        createUniform("colourTexture");
    }

    public void connectTextureUnits(){
        setUniform("colourTexture", 0);
    }
}
