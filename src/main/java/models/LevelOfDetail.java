package models;

import org.joml.Vector3d;
import org.joml.Vector3f;
import terrain.CubeQuadTree;
import terrain.QuadTree;

public interface LevelOfDetail {
    public CubeQuadTree getLevelOfDetailTree(Vector3d cameraPos);
}
