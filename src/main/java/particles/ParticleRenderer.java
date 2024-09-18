package particles;

import java.nio.FloatBuffer;
import java.util.List;
import java.util.Map;

import Textures.ParticleTexture;
import models.Mesh;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;


import renderEngine.Camera;
import renderEngine.Loader;
import renderEngine.Transformation;


public class ParticleRenderer {
	
	private static final float[] VERTICES = {-0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, -0.5f};
	private static final int MAX_INSTANCES = 1000000;
	private static final int INSTANCE_DATA_LENGTH = 21;

	private static final FloatBuffer buffer = BufferUtils.createFloatBuffer(MAX_INSTANCES * INSTANCE_DATA_LENGTH);

	private Mesh quad;
	private ParticleShader shader;

	private Loader loader;
	private int vbo;
	private int pointer = 0;

	private Matrix4d reusableModelMatrix = new Matrix4d();
	private Matrix4d reusableModelViewMatrix= new Matrix4d();

	protected ParticleRenderer(Loader loader) throws Exception {
		this.loader = loader;
		this.vbo = loader.createEmptyVbo(INSTANCE_DATA_LENGTH * MAX_INSTANCES);
		quad = loader.loadToVAO(VERTICES, 2);
		loader.addInstancedAttribute(quad.getVaoID(), vbo, 1, 4, INSTANCE_DATA_LENGTH, 0);
		loader.addInstancedAttribute(quad.getVaoID(), vbo, 2, 4, INSTANCE_DATA_LENGTH, 4);
		loader.addInstancedAttribute(quad.getVaoID(), vbo, 3, 4, INSTANCE_DATA_LENGTH, 8);
		loader.addInstancedAttribute(quad.getVaoID(), vbo, 4, 4, INSTANCE_DATA_LENGTH, 12);
		loader.addInstancedAttribute(quad.getVaoID(), vbo, 5, 4, INSTANCE_DATA_LENGTH, 16);
		loader.addInstancedAttribute(quad.getVaoID(), vbo, 6, 1, INSTANCE_DATA_LENGTH, 20);
		GL30.glBindVertexArray(quad.getVaoID());
		GL30.glEnableVertexAttribArray(0);
		GL30.glEnableVertexAttribArray(1);
		GL30.glEnableVertexAttribArray(2);
		GL30.glEnableVertexAttribArray(3);
		GL30.glEnableVertexAttribArray(4);
		GL30.glEnableVertexAttribArray(5);
		GL30.glEnableVertexAttribArray(6);
		GL30.glBindVertexArray(0);
		shader = new ParticleShader();
		shader.start();
		shader.loadProjectionMatrix(Transformation.getProjectionMatrix());
		shader.unbind();
	}
	
	protected void render(Map<ParticleTexture, List<Particle>> particles, Camera camera){
		Matrix4d viewMatrix = Transformation.getViewMatrix(camera);
		prepare();
		for(ParticleTexture tex : particles.keySet()){
			bindTexture(tex);
			List<Particle> particleList = particles.get(tex);
			pointer = 0;
			float[] vboData = new float[particleList.size() * INSTANCE_DATA_LENGTH];

			for(Particle particle : particleList){
				updateModelViewMatrix(particle.getPosition(), particle.getRotation(), particle.getScale(), viewMatrix, vboData);
				updateTexCoordInfo(particle, vboData);
			}
			loader.updateVbo(vbo, vboData, buffer);
			GL31.glDrawArraysInstanced(GL30.GL_TRIANGLE_STRIP, 0, quad.getVertexCount(), particleList.size());

		}
		finishRendering();
	}

	private void bindTexture(ParticleTexture tex){
		GL30.glActiveTexture(GL30.GL_TEXTURE0);
		GL30.glBindTexture(GL30.GL_TEXTURE_2D, tex.getTextureID());
		if(tex.isAdditive()){
			GL30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE);
		}
		else {
			GL30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
		}
		shader.loadNumberOfRows(tex.getRows());
	}

	protected void cleanUp(){
		shader.cleanUp();
	}

	private void updateTexCoordInfo(Particle particle, float[] data){
		data[pointer++] = particle.getTexOffset1().x;
		data[pointer++] = particle.getTexOffset1().y;
		data[pointer++] = particle.getTexOffset2().x;
		data[pointer++] = particle.getTexOffset2().y;
		data[pointer++] = particle.getBlend();
	}

	private void updateModelViewMatrix(Vector3d position, float rotation, float scale, Matrix4d viewMatrix, float[] vboData){
		//Super costly for cpu
		Matrix4d modelMatrix = reusableModelMatrix.identity();
		modelMatrix.translate(position, modelMatrix);
		modelMatrix.m00(viewMatrix.m00());
		modelMatrix.m01(viewMatrix.m10());
		modelMatrix.m02(viewMatrix.m20());
		modelMatrix.m10(viewMatrix.m01());
		modelMatrix.m11(viewMatrix.m11());
		modelMatrix.m12(viewMatrix.m21());
		modelMatrix.m20(viewMatrix.m02());
		modelMatrix.m21(viewMatrix.m12());
		modelMatrix.m22(viewMatrix.m22());
		modelMatrix.rotate(Math.toRadians(rotation), new Vector3d(0,0,1));
		modelMatrix.scale(new Vector3d(scale,scale,scale));
		reusableModelViewMatrix.set(viewMatrix);
		Matrix4d modelViewMatrix = reusableModelViewMatrix.mul(modelMatrix);
		storeMatrixData(modelViewMatrix, vboData);
	}

	private void storeMatrixData(Matrix4d matrix, float[] vboData){
		vboData[pointer++] = (float) matrix.m00();
		vboData[pointer++] = (float) matrix.m01();
		vboData[pointer++] = (float) matrix.m02();
		vboData[pointer++] = (float) matrix.m03();
		vboData[pointer++] = (float) matrix.m10();
		vboData[pointer++] = (float) matrix.m11();
		vboData[pointer++] = (float) matrix.m12();
		vboData[pointer++] = (float) matrix.m13();
		vboData[pointer++] = (float) matrix.m20();
		vboData[pointer++] = (float) matrix.m21();
		vboData[pointer++] = (float) matrix.m22();
		vboData[pointer++] = (float) matrix.m23();
		vboData[pointer++] = (float) matrix.m30();
		vboData[pointer++] = (float) matrix.m31();
		vboData[pointer++] = (float) matrix.m32();
		vboData[pointer++] = (float) matrix.m33();
	}


	private void prepare(){
		shader.start();
		GL30.glBindVertexArray(quad.getVaoID());
		GL30.glEnable(GL30.GL_BLEND);
		GL30.glDepthMask(false);
	}
	
	private void finishRendering(){
		GL30.glDepthMask(true);
		GL30.glDisable(GL30.GL_BLEND);
		GL30.glBindVertexArray(0);
		shader.unbind();
	}

}
