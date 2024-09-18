package particles;



import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import shaders.ShaderProgram;

public class ParticleShader extends ShaderProgram {

	private static final String VERTEX_FILE = "particles/particleVShader.txt";
	private static final String FRAGMENT_FILE = "particles/particleFShader.txt";

	private int location_modelViewMatrix;
	private int location_projectionMatrix;

	public ParticleShader() throws Exception {
		super(VERTEX_FILE, FRAGMENT_FILE);
		createUniform("projectionMatrix");
		createUniform("numberOfRows");
	}

	protected void loadNumberOfRows(float rows){
		setUniform("numberOfRows", rows);
	}

	protected void loadProjectionMatrix(Matrix4f projectionMatrix) {
		super.setUniform("projectionMatrix", projectionMatrix);
	}

}
