package models;

import org.joml.Vector3f;
import terrain.FastNoiseLite;
import terrain.QuadTree;

public abstract class CelestialBody implements LevelOfDetail{
    protected Vector3f worldSpacePos;
    protected int radius;
    protected boolean hasHeight = true;
    protected int mainTex = 0;

    public CelestialBody(Vector3f worldSpacePos, int radius){
        this.radius = radius;
        this.worldSpacePos = worldSpacePos;
    }

    public boolean hasHeight() {
        return hasHeight;
    }

    public int getMainTextureID(){
        return mainTex;
    }

    public int getRadius() {
        return radius;
    }

    public Vector3f getWorldSpacePos() {
        return worldSpacePos;
    }
}
