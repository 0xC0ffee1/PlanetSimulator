package gaussianBlur;

import shaders.ShaderProgram;

public class VerticalBlurShader extends ShaderProgram{

	private static final String VERTEX_FILE = "postprocessing/gaussianBlur/verticalBlurVertex.txt";
	private static final String FRAGMENT_FILE = "postprocessing/gaussianBlur/blurFragment.txt";
	
	private int location_targetHeight;
	
	protected VerticalBlurShader() throws Exception {
		super(VERTEX_FILE, FRAGMENT_FILE);
		createUniform("targetHeight");
	}
	
	protected void loadTargetHeight(float height){ setUniform("targetHeight", height);
	}
}
