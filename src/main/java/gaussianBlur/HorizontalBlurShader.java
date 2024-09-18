package gaussianBlur;

import shaders.ShaderProgram;

public class HorizontalBlurShader extends ShaderProgram {

	private static final String VERTEX_FILE = "postprocessing/gaussianBlur/horizontalBlurVertex.txt";
	private static final String FRAGMENT_FILE = "postprocessing/gaussianBlur/blurFragment.txt";
	
	protected HorizontalBlurShader() throws Exception {
		super(VERTEX_FILE, FRAGMENT_FILE);
		createUniform("targetWidth");
	}

	protected void loadTargetWidth(float width){
		setUniform("targetWidth", width);
	}

	
}
