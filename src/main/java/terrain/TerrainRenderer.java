package terrain;

import Misc.DisplayTime;
import Misc.FrustumCullingFilter;
import Misc.Maths;
import Misc.WorldConstants;
import Textures.*;
import models.Entity;
import models.Light;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL30;
import renderEngine.Camera;
import renderEngine.Loader;
import renderEngine.Renderer;
import renderEngine.Transformation;

import java.util.*;



public class TerrainRenderer extends Renderer {
    private TerrainShader terrainShader;
    private List<TerrainChunk> terrain;
    private TextureArray textureArray;
    private TextureArray normalArray;
    private TextureArray roughnessArray;
    private TextureArray occlusionArray;
    private TextureArray metalArray;
    private FrustumCullingFilter filter;
    int noiseMap;
    public static Light light = new Light(TerrainChunkManager.SUN.getWorldSpacePos(), new Vector3f(1,1,1));
    public TerrainRenderer(Camera camera, FrustumCullingFilter filter) throws Exception{
        super(camera);
        this.filter = filter;
        this.terrainShader = new TerrainShader();
        terrain = new ArrayList<>();
        textureArray = new TerrainTextureArray();
        normalArray = new NormalTextureArray();
        roughnessArray = new RoughnessTextureArray();
        occlusionArray = new AmbientOcclusionTextureArray();
        metalArray = new MetalTextureArray();
        noiseMap = new Texture(WorldConstants.RES_DIR + "terrain/noiseMap.png").getID();

//        CubeQuadTree quadTree = new CubeQuadTree(TerrainChunkManager.RADIUS);
//        for(Matrix4f m : quadTree.getTransforms()){
//            MeshData meshData = TerrainMeshBuilder.generateSphere(m, new Vector3f(0,0,0), TerrainChunkManager.RADIUS, TerrainChunkManager.RADIUS*2, 1000);
//            //MeshData meshData = TerrainMeshBuilder.generateTerrainData(0,0,500);
//            Mesh mesh = loader.loadToVAO(meshData.vertices,meshData.indices,meshData.textureCoords,meshData.normals);
//            TerrainChunk t = new TerrainChunk(0, m, new Vector3f(0,100,0), new Boundary(-TerrainChunkManager.RADIUS,-TerrainChunkManager.RADIUS,0,TerrainChunkManager.RADIUS,TerrainChunkManager.RADIUS,0));
//            t.setMesh(mesh);
//            terrain.add(t);
//        }
    }

    public void render(HashSet<TerrainChunk> chunks, Vector4f planeClip){

        terrainShader.start();
        terrainShader.setUniform("texArray", 0);
        terrainShader.setUniform("noiseMap", 1);
        terrainShader.setUniform("normalMapArray", 2);
        terrainShader.setUniform("roughnessArray", 3);
        terrainShader.setUniform("occlusionArray", 4);
        terrainShader.setUniform("metalArray", 5);
        terrainShader.setUniform("cameraPos", camera.getPosition());
        GL30.glActiveTexture(GL30.GL_TEXTURE0);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, textureArray.getID());
        GL30.glActiveTexture(GL30.GL_TEXTURE1);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, noiseMap);
        GL30.glActiveTexture(GL30.GL_TEXTURE2);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, normalArray.getID());
        GL30.glActiveTexture(GL30.GL_TEXTURE3);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, roughnessArray.getID());
        GL30.glActiveTexture(GL30.GL_TEXTURE4);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, occlusionArray.getID());
        GL30.glActiveTexture(GL30.GL_TEXTURE5);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, metalArray.getID());
        terrainShader.setUniform(light);
        Matrix4d view = Transformation.getViewMatrix(camera);
        Matrix4f proj = Transformation.getProjectionMatrix();
        terrainShader.setUniform("viewMatrix", view);

        terrainShader.setUniform("projectionMatrix", proj);
        terrainShader.setUniform("skyColour", new Vector3f(1,1,1));
        terrainShader.setUniform("time", DisplayTime.getTime());
        terrainShader.setUniform("planeClip", planeClip);

        //GL30.glPolygonMode( GL30.GL_FRONT_AND_BACK, GL30.GL_LINE );

        for(TerrainChunk terrainChunk : chunks){
//            Vector3f p = terrainChunk.getSphereCenter();
//            double distance = camera.getPosition().distance(p);
//            if(Math.acos((Math.pow(terrainChunk.getRadius(), 2) + Math.pow(distance, 2) -
//                    Math.pow(camera.getPosition().distance(new Vector3f(0,0,0)), 2)) / (2 * terrainChunk.getRadius() * distance)) < 1.45f){
//                continue;
//            }
//            if(new Vector3f(terrainChunk.getO_translation()).normalize().dot(camera.getSightVector()) < 0.5){
//                continue;
//            }
//            Vector3f normal = new Vector3f(terrainChunk.getPosition()).normalize().mul(terrainChunk.getSize());
//            Maths.transformDirection(normal, terrainChunk.getGroup());

            //System.out.println(filter.testSphere(terrainChunk.getSphereCenter(), terrainChunk.getRadius()));
            if(!terrainChunk.hasMesh()) continue;
            //System.out.println(terrainChunk.getPosition());
            //System.out.println(terrainChunk.getGroup());

            terrainShader.setUniform("radius", (float) terrainChunk.getRadius());
            Entity e = new Entity();
            terrainChunk.update();
            terrainShader.setUniform("mainTexID", terrainChunk.getCelestialBody().getMainTextureID());
            terrainShader.setUniform("planetPos", terrainChunk.getO_translation());
            e.setPosition(terrainChunk.getTranslation().x, terrainChunk.getTranslation().y, terrainChunk.getTranslation().z);
            terrainShader.setUniform("transformationMatrix", Transformation.getTransformation(e));
            terrainChunk.getMesh().render();
        }


        //GL30.glPolygonMode( GL30.GL_FRONT_AND_BACK, GL30.GL_FILL );
        terrainShader.unbind();
    }
}
