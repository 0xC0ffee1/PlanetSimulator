package Misc;

public class DisplayTime {
    private static float time = 0;
    private static float waveTime = 0;
    private static float refreshTime = 0;
    private static final float WAVE_SPEED = 0.02f;

    public static void refreshTime(double time){
        refreshTime = (float) time;
        DisplayTime.time += time;
        waveTime += WAVE_SPEED * time;
        waveTime %= 1;
    }
    public static float getTime(){
         return time;
    }
    public static float getWaveTime(){
        return waveTime;
    }
    public static float getRefreshTime(){
        return refreshTime;
    }
}
