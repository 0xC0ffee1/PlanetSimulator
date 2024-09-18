package particles;

import Textures.ParticleTexture;

import org.joml.Matrix4d;
import org.joml.Matrix4f;
import renderEngine.Camera;
import renderEngine.Loader;

import java.util.*;

public class ParticleHandler {
    private static Map<ParticleTexture, List<Particle>> particles = new HashMap<>();
    private static ParticleRenderer renderer;

    public static void init(Loader loader) throws Exception {
        renderer = new ParticleRenderer(loader);
    }
    public static void update(Camera camera){
        Iterator<Map.Entry<ParticleTexture, List<Particle>>> mapIter = particles.entrySet().iterator();
        while (mapIter.hasNext()){
            List<Particle> list = mapIter.next().getValue();
            Iterator<Particle> iterator = list.iterator();
            while (iterator.hasNext()){
                Particle p = iterator.next();
                boolean stillAlive = p.update(camera);
                if(!stillAlive){
                    iterator.remove();
                    if(list.isEmpty()){
                        mapIter.remove();
                    }
                }
            }
            //list.sort(Comparator.comparing(Particle::getDistance));
            //InsertionSort.sortHighToLow(list);
        }
    }
    public static void renderParticles(Camera camera){
        renderer.render(particles, camera);
    }
    public static void cleanUp(){
        renderer.cleanUp();
    }
    public static void addParticle(Particle particle){
        if(!particles.containsKey(particle.getTexture())){
            particles.put(particle.getTexture(), new ArrayList<>());
        }
        particles.get(particle.getTexture()).add(particle);
    }
}
