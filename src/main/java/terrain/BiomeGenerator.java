package terrain;

import org.joml.Vector3f;

public class BiomeGenerator {
    private static int seed = 12;
    private static final FastNoiseLite simplexNoise1 = new FastNoiseLite(seed);
    private static final FastNoiseLite simplexNoise2 = new FastNoiseLite(seed);
    static {
        simplexNoise1.SetFrequency(0.05f);
        simplexNoise2.SetFrequency(0.5f);
        simplexNoise1.SetFractalType(FastNoiseLite.FractalType.FBm);
        simplexNoise2.SetFractalType(FastNoiseLite.FractalType.FBm);
        simplexNoise1.SetFractalOctaves(4);
        simplexNoise2.SetFractalOctaves(2);
    }

    public static float generate(Vector3f pos, float radius){
        float height = pos.z;
        if(height > 400){
            return 11;
        }
        else if(height > 100){
            return 8;
        }
        else if(height > -20){
            return 1;
        }
        else {
            return 9;
        }
    }
}
