package terrain;

import events.EventBus;
import events.TerrainRebuildEvent;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Objects;

public class TerrainRebuilder {
    private TerrainChunkManager terrainChunkManager;
    private ArrayDeque<TerrainChunk> chunkQue;
    private final HashSet<TerrainChunk> rebuildedChunks;

    private boolean busy = false;

    private boolean halt = false;

    public TerrainRebuilder(TerrainChunkManager terrainChunkManager){
        chunkQue = new ArrayDeque<>();
        rebuildedChunks = new HashSet<>();
        this.terrainChunkManager = terrainChunkManager;
    }

    public void update(){

        if (!chunkQue.isEmpty()){
            TerrainChunk terrainChunk = chunkQue.removeFirst();
            if(!terrainChunk.hasMesh()) terrainChunk.loadMesh();
            rebuildedChunks.add(terrainChunk);
        }
        else if(!halt){
            halt = true;
            signalManager();
        }
    }

    public boolean isHalt() {
        return halt;
    }

    public synchronized void signalManager(){

        for(TerrainChunk terrainChunk : terrainChunkManager.getRenderList()){
            if(!rebuildedChunks.contains(terrainChunk)){
                terrainChunk.freeMemory();
            }
        }
        TerrainChunkManager.getTerrainEventBus().send(new TerrainRebuildEvent(new HashSet<>(rebuildedChunks)));
    }

    public void setHalt(boolean halt) {
        this.halt = halt;
    }

    public void addToQue(HashSet<TerrainChunk> chunkQue) {
        setBusy(true);
        rebuildedChunks.clear();
        this.chunkQue.addAll(chunkQue);
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    public boolean busy(){
        return busy;
    }
}
