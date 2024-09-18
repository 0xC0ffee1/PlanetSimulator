package bloom;

import shaders.ShaderProgram;

public class CombineShader extends ShaderProgram {

	private static final String VERTEX_FILE = "postprocessing/bloom/simpleVertex.txt";
	private static final String FRAGMENT_FILE = "postprocessing/bloom/combineFragment.txt";
	
	public CombineShader() throws Exception {
		super(VERTEX_FILE, FRAGMENT_FILE);
		createUniform("colourTexture");
		createUniform("highlightTexture");
	}

	public void connectTextureUnits(){
		setUniform("colourTexture", 0);
		setUniform("highlightTexture", 1);
	}

}
