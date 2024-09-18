package Misc;

import org.joml.Vector3f;

public abstract class WorldConstants {
    public static String RES_DIR = "";

    public static float GRAVITY = -50;

    public static boolean FULLSCREEN = false;
    public static float MOUSE_SENSITIVITY = 0.5f;
    public static boolean NO_GUI = false;
    public static boolean I_MOVE = false;
    public static final int CHUNKSIZE = 32;
    public static final int CHUNKLOAD_DISTANCE = 7;
    public static int CHUNK_UNLOAD_DISTANCE = CHUNKLOAD_DISTANCE *3*CHUNKSIZE;
    public static final int RENDER_DISTANCE = 12;
    public static final int CHUNKAREA = CHUNKSIZE * CHUNKSIZE;
    public static final int CHUNKVOLUME = CHUNKSIZE*CHUNKSIZE*CHUNKSIZE;
    public static Vector3f SKYCOLOUR = new Vector3f(136/255f, 158/255f, 176/255f);
    public static float FOV = (float) Math.toRadians(90.0f);
    public static final float Z_NEAR = 1f;
    public static final float Z_FAR = 35000000000.0f;
    public static int WINDOW_WIDTH =  2550;
    public static int WINDOW_HEIGHT = 1440;
    public static float WATER_LEVEL = 25f;

    public static int TEXTURE_COUNT = 15;
}
