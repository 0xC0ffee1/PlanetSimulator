package Textures;

import Misc.WorldConstants;

public class NormalTextureArray extends TextureArray{

    public NormalTextureArray() {
        super(1024, WorldConstants.TEXTURE_COUNT, "terrain/");
    }

    @Override
    public void loadLayers() {
        loadToLayer("Rock_038_normal.jpg", 0);
        loadToLayer("grass_normal.jpg", 1);
        loadToLayer("rock2_normal.jpg", 2);
        loadToLayer("rocks3_normal.jpg", 3);
        loadToLayer("obsidian_normal.jpg", 4);
        loadToLayer("metalplate_normal.jpg", 5);
        loadToLayer("glass_normal.jpg", 6);
        loadToLayer("ground_normal.jpg", 7);
        loadToLayer("cliff_normal.jpg", 8);
        loadToLayer("mud_normal.jpg", 9);
        loadToLayer("wetrocks2_normal.jpg", 10);
        loadToLayer("snow_normal.jpg", 11);
        loadToLayer("quartz_normal.jpg", 12);
        loadToLayer("water_normal.jpg", 13);
    }
}
