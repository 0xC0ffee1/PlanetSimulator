package postProcessing;

import atmosphere.AtmosFilter;
import bloom.BrightFilter;
import bloom.CombineFilter;
import gaussianBlur.HorizontalBlur;
import gaussianBlur.VerticalBlur;
import models.CelestialBody;
import models.Mesh;
import models.Planet;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import particles.ParticleHandler;
import renderEngine.Camera;
import renderEngine.Loader;
import renderEngine.Window;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PostProcessing {
	
	private static final float[] POSITIONS = { -1, 1, -1, -1, 1, 1, 1, -1 };	
	private static Mesh quad;
	private static ContrastChanger contrastChanger;
	private static BrightFilter brightFilter;
	private static HorizontalBlur hBlur;
	private static VerticalBlur vBlur;
	private static HorizontalBlur hBlur2;
	private static VerticalBlur vBlur2;
	private static CombineFilter combineFilter;
	private static EmptyFilter emptyFilter;
	private static AtmosFilter atmosFilter;
	private static OceanFilter oceanFilter;
	private static Camera camera;
	public static void init(Loader loader, Window window, Camera camera) throws Exception{
		quad = loader.loadToVAO(POSITIONS, 2);
		PostProcessing.camera = camera;
		//contrastChanger = new ContrastChanger(window.getWidth()/2, window.getHeight()/2, window);
		atmosFilter = new AtmosFilter(window.getWidth(), window.getHeight(), window, camera);
		oceanFilter = new OceanFilter(window.getWidth(), window.getHeight(), window, camera);
		brightFilter = new BrightFilter(window.getWidth()/2, window.getHeight()/2, window);
		hBlur = new HorizontalBlur(window.getWidth()/8, window.getHeight()/8, window);
		vBlur = new VerticalBlur(window.getWidth()/8, window.getHeight()/8, window);
		hBlur2 = new HorizontalBlur(window.getWidth()/2, window.getHeight()/2, window);
		vBlur2 = new VerticalBlur(window.getWidth()/2, window.getHeight()/2, window);
		combineFilter = new CombineFilter();
		emptyFilter = new EmptyFilter();
	}
	
	public static void doPostProcessing(int colourTexture, int depthTexture, Collection<CelestialBody> planets){
		start();
		brightFilter.render(colourTexture);
		hBlur2.render(brightFilter.getOutputTexture());
		vBlur2.render(hBlur2.getOutputTexture());
		hBlur.render(vBlur2.getOutputTexture());
		vBlur.render(hBlur.getOutputTexture());

		for(CelestialBody celestialBody : planets){
			if(celestialBody instanceof Planet){
				Planet planet = (Planet) celestialBody;
				if(planet.getName().equalsIgnoreCase("earth")){
					//oceanFilter.render(colourTexture, depthTexture, planet);

				}
				atmosFilter.render(colourTexture, depthTexture, planet);
			}

		}

//		for(Planet planet : planets){
//			if(planet.getName().equalsIgnoreCase("moon")){
//				atmosFilter2.render(atmosFilter.getOutputTexture(), depthTexture, planet);
//			}
//		}

		combineFilter.render(atmosFilter.getOutputTexture(), vBlur.getOutputTexture());
		end();
	}

	public static void renderFbo(int colourTexture){
		start();
		emptyFilter.render(colourTexture);
		end();
	}
	
	public static void cleanUp(){
		contrastChanger.cleanUp();
		brightFilter.cleanUp();
		hBlur.cleanUp();
		vBlur.cleanUp();
		hBlur2.cleanUp();
		vBlur2.cleanUp();
		combineFilter.cleanUp();
	}
	
	private static void start(){
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}
	
	private static void end(){
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}


}
