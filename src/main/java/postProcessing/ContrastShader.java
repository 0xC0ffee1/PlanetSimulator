package postProcessing;

import shaders.ShaderProgram;

public class ContrastShader extends ShaderProgram {

	private static final String VERTEX_FILE = "src/postprocessing/contrastVertex.txt";
	private static final String FRAGMENT_FILE = "src/postprocessing/contrastFragment.txt";
	
	public ContrastShader() throws Exception {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
}
