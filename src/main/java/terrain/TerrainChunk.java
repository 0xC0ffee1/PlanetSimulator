package terrain;

import models.CelestialBody;
import models.Mesh;
import org.joml.*;
import renderEngine.Camera;
import renderEngine.LiteLoader;

import java.util.Objects;
import java.util.UUID;

public class TerrainChunk{
    private int index;
    private Matrix4f group;

    private Vector3f position;
    private Boundary bounds;
    private LiteLoader liteLoader;
    private Mesh mesh;
    private MeshData meshData;

    private Vector3f translation;
    private int radius;

    private Vector3d sphereCenter;

    private Vector3d camPos;

    private Vector3d origin;

    private Vector3f o_translation;

    private CelestialBody celestialBody;

    public TerrainChunk(int index, Matrix4f group, Vector3f position, Boundary bounds, Vector3f translation, int radius, Vector3d sphereCenter, Vector3d camPos, CelestialBody celestialBody) {
        this.position = position;
        this.liteLoader = new LiteLoader();
        this.translation = translation;
        this.index = index;
        this.celestialBody = celestialBody;
        this.sphereCenter = sphereCenter;
        this.group = group;
        this.bounds = bounds;
        this.radius = radius;
        this.camPos = camPos;
        this.origin = new Vector3d(camPos);
        this.o_translation = new Vector3f(translation);
    }


    public Vector3d getOrigin() {
        return origin;
    }

    public CelestialBody getCelestialBody() {
        return celestialBody;
    }

    public Vector3f getO_translation() {
        return o_translation;
    }

    public Vector3d getSphereCenter() {
        return sphereCenter;
    }

    public int getRadius() {
        return radius;
    }

    public Vector3f getTranslation() {
        return translation;
    }

    public int getIndex() {
        return index;
    }

    public Matrix4f getGroup() {
        return group;
    }

    public void loadMesh(){
        if(meshData == null || liteLoader == null) return;
        this.mesh = liteLoader.loadToVAO(meshData.vertices,meshData.indices,meshData.textureCoords, meshData.normals, meshData.texSlices);
    }


    public void freeMemory(){
        if(mesh == null) return;
        mesh = null;
        meshData = null;
        liteLoader.cleanUp();
        liteLoader = null;
    }

    public boolean hasMesh(){
        return mesh != null;
    }

    public void setMeshData(MeshData meshData) {
        this.meshData = meshData;
    }

    public MeshData getMeshData() {
        return meshData;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void update(){
        Vector3d delta = new Vector3d(origin).sub(camPos);
        translation.set(delta);
    }


    public Boundary getBounds() {
        return bounds;
    }

    public float getSize(){
        return bounds.size().x;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TerrainChunk that = (TerrainChunk) o;
        return that.getPosition().equals(position) && that.getSize() == getSize() && that.index == index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(position.x, position.y, index, getSize());
    }
}
