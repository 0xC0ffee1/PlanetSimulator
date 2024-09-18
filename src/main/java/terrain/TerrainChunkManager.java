package terrain;

import events.EventBus;
import events.TerrainRebuildEvent;
import models.CelestialBody;
import models.Planet;
import models.Star;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import renderEngine.Camera;
import renderEngine.Window;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class TerrainChunkManager extends Thread{
    private static EventBus terrainEventBus;
    private TerrainThreadManager terrainThreadManager;
    public static int RADIUS = 4000 * 1000;
    private final Camera camera;
    private HashSet<TerrainChunk> chunks;
    private HashSet<CelestialBody> planets;

    public static Planet EARTH;
    public static Star SUN;


    public TerrainChunkManager(Camera camera){
        terrainEventBus = new EventBus();
        terrainThreadManager = new TerrainThreadManager(this, camera);
        this.camera = camera;
        this.chunks = new HashSet<>();
        this.planets = new HashSet<>();
        EARTH = new Planet("Earth", new Vector3f(0,0,0), RADIUS, 4);
        SUN = new Star("sun", new Vector3f(-RADIUS*300,RADIUS*400,0), RADIUS*10);
        planets.add(EARTH);
        planets.add(SUN);
        planets.add(new Planet("Mars", new Vector3f(-RADIUS*2,RADIUS*3,RADIUS*5), RADIUS/2, 5));
        terrainEventBus.subscribe(TerrainRebuildEvent.class, this::onTerrainRebuild);
    }

    public static EventBus getTerrainEventBus(){
        return terrainEventBus;
    }


    public void startTerrainGen(){
        this.start();
    }

    @Override
    public void run() {
        while (!Window.shouldClose()){
            if(terrainThreadManager.getTerrainRebuilder().busy()) {
                continue;
            }
            HashSet<TerrainChunk> newChunks = new HashSet<>();
            Vector3d camPos = camera.getPosition();
            for(CelestialBody planet : planets){
                List<SideData> sides = new ArrayList<>(planet.getLevelOfDetailTree(camPos).getChildren());
                for(int i = 0; i < sides.size(); i++){
                    for(QuadTree c : sides.get(i).quadTree.getLeafNodes()){
                        Vector3f center = c.getCenter();
                        TerrainChunk terrainChunk = new TerrainChunk(i, sides.get(i).transform,
                                new Vector3f(center.x,center.y,center.z),
                                c.boundary, c.getTranslate(), planet.getRadius(),
                                c.getSphereCenter(), camPos, planet);
                        newChunks.add(terrainChunk);
                    }
                }
            }

            HashSet<TerrainChunk> intersection = new HashSet<>();
            for(TerrainChunk terrainChunk : chunks){
                if(newChunks.contains(terrainChunk)){
                    intersection.add(terrainChunk);
                }
            }
            HashSet<TerrainChunk> difference = new HashSet<>(newChunks);
            for(TerrainChunk terrainChunk : chunks){
                difference.remove(terrainChunk);
            }
            if(difference.isEmpty()) continue;
            newChunks = intersection;
            newChunks.addAll(difference);
            try {
                terrainThreadManager.addRebuild(newChunks);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public HashSet<TerrainChunk> getRenderList(){
        return chunks;
    }


    public HashSet<CelestialBody> getPlanets() {
        return planets;
    }

    public void onTerrainRebuild(TerrainRebuildEvent event){
        this.chunks = event.getNewChunks();
        terrainThreadManager.getTerrainRebuilder().setBusy(false);
        terrainThreadManager.getTerrainRebuilder().setHalt(false);
    }

    public void update(){

        terrainThreadManager.updateMainThread();
    }
}
