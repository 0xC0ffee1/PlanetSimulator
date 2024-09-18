package terrain;

import org.joml.Vector3d;
import org.joml.Vector3f;
import renderEngine.Camera;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class TerrainGenerationTask {
    private Collection<TerrainChunk> chunksToGen;
    private Collection<TerrainChunk> generated;

    public TerrainGenerationTask(Collection<TerrainChunk> chunksToGen){
        this.chunksToGen = chunksToGen;
        this.generated = new HashSet<>();
    }

    public Collection<TerrainChunk> getGenerated() {
        return generated;
    }

    public void generate(){
        for(TerrainChunk terrainChunk : chunksToGen){

            if(!terrainChunk.hasMesh()){
                MeshData meshData = TerrainMeshBuilder.generateSphereChunk(terrainChunk.getGroup(),
                        terrainChunk.getPosition(), terrainChunk.getRadius(),
                        terrainChunk.getSize(), 128, terrainChunk.getOrigin(), terrainChunk.getO_translation(), terrainChunk.getCelestialBody().hasHeight());
                terrainChunk.setMeshData(meshData);
            }
            generated.add(terrainChunk);
        }
    }
}
