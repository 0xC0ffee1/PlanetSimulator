package terrain;

import com.google.common.collect.Lists;
import org.joml.Vector3d;
import org.joml.Vector3f;
import renderEngine.Camera;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

public class TerrainThreadManager {
    private final TerrainRebuilder terrainRebuilder;
    private final TerrainChunkManager terrainChunkManager;
    private final ThreadPoolExecutor executorService;
    private final Camera camera;

    public TerrainThreadManager(TerrainChunkManager terrainChunkManager, Camera camera){
        this.executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.terrainRebuilder = new TerrainRebuilder(terrainChunkManager);
        this.terrainChunkManager = terrainChunkManager;
        this.camera = camera;
    }
    public void addRebuild(HashSet<TerrainChunk> terrainChunks) throws ExecutionException, InterruptedException {
        ArrayList<TerrainChunk> tChunks = new ArrayList<>(terrainChunks);

        List<List<TerrainChunk>> subLists = Lists.partition(tChunks, 25);

        List<Future<TerrainGenerationTask>> futures = new ArrayList<>();
        for(List<TerrainChunk> list : subLists){
            Future<TerrainGenerationTask> taskFuture = executorService.submit(() -> {
                TerrainGenerationTask task = new TerrainGenerationTask(list);
                task.generate();
                return task;
            });
            futures.add(taskFuture);
        }
        HashSet<TerrainChunk> finalC = new HashSet<>();
        for(int i = 0; i < futures.size(); i++){
            Future<TerrainGenerationTask> taskFuture = futures.get(i);
            TerrainGenerationTask t = taskFuture.get();
            finalC.addAll(t.getGenerated());
        }
        terrainRebuilder.addToQue(finalC);
    }


    public void updateMainThread(){
        //Has to be done in main thread because of OpenGL limitations

        terrainRebuilder.update();
    }

    public TerrainRebuilder getTerrainRebuilder() {
        return terrainRebuilder;
    }

}
