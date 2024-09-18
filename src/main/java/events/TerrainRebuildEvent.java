package events;

import terrain.TerrainChunk;

import java.util.HashSet;

public class TerrainRebuildEvent extends Event{
    private HashSet<TerrainChunk> newChunks;

    public TerrainRebuildEvent(HashSet<TerrainChunk> newChunks){
        this.newChunks = newChunks;

    }
    public HashSet<TerrainChunk> getNewChunks() {
        return newChunks;
    }

}
