package models;

import org.joml.Vector3d;
import org.joml.Vector3f;
import terrain.CubeQuadTree;
import terrain.QuadTree;

import java.util.Objects;

public class Planet extends CelestialBody {
    private String name;

    public Planet(String name, Vector3f pos, int radius, int tex) {
        super(pos, radius);
        this.name = name;
        this.mainTex = tex;
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
        Planet planet = (Planet) o;
        return Objects.equals(name, planet.name) && worldSpacePos.equals(planet.worldSpacePos) && radius == planet.radius;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, radius, worldSpacePos);
    }

    @Override
    public int getMainTextureID() {
        return mainTex;
    }
}
