package events;

import terrain.TerrainChunk;

import terrain.TerrainGenerationTask;

import java.util.HashSet;

public class ThreadFinishEvent extends Event{
    private TerrainGenerationTask task;

    public ThreadFinishEvent(TerrainGenerationTask task){
        this.task = task;
    }

    public TerrainGenerationTask getTask() {
        return task;
    }
}
