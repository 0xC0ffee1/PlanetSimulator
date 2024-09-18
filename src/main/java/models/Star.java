package models;

import org.joml.Vector3d;
import org.joml.Vector3f;
import terrain.CubeQuadTree;

import java.util.Objects;

public class Star extends CelestialBody implements LevelOfDetail{
    private String name;
    private int magnitude;

    public Star(String name, Vector3f pos, int radius) {
        super(pos, radius);
        this.name = name;
        hasHeight = false;
    }

    public String getName() {
        return name;
    }

    public float getAtmosphereRadius(){
        return radius + (radius * 0.1f);
    }

    @Override
    public CubeQuadTree getLevelOfDetailTree(Vector3d cameraPos) {
        CubeQuadTree quadTree = new CubeQuadTree(worldSpacePos, radius);
        quadTree.insert(cameraPos);
        return quadTree;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Star star = (Star) o;
        return Objects.equals(name, star.name) && worldSpacePos.equals(star.worldSpacePos) && radius == star.radius;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, radius, worldSpacePos);
    }

    @Override
    public int getMainTextureID() {
        return 14;
    }
}
