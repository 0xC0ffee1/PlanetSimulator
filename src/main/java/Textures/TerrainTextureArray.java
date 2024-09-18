package Textures;

import Misc.WorldConstants;

public class TerrainTextureArray extends TextureArray{

    public TerrainTextureArray() {
        super(1024, WorldConstants.TEXTURE_COUNT, "terrain/");
    }

    @Override
    public void loadLayers() {
        loadToLayer("grass_diffuse.jpg", 1);
        loadToLayer("Rock_038_baseColor.jpg", 0);

        loadToLayer("rock2.jpg", 2);
        loadToLayer("rocks3_diffuse.jpg", 3);
        loadToLayer("obsidian_diffuse.jpg", 4);
        loadToLayer("metalplate_diffuse.jpg", 5);
        loadToLayer("glass_diffuse.jpg", 6);
        loadToLayer("ground_diffuse.jpg", 7);
        loadToLayer("cliff_diffuse.jpg", 8);
        loadToLayer("mud_diffuse.jpg", 9);
        loadToLayer("wetrocks2_diffuse.jpg", 10);
        loadToLayer("snow_diffuse.jpg", 11);
        loadToLayer("quartz_diffuse.jpg", 12);
        loadToLayer("water_diffuse.jpg",13);
        loadToLayer("lava_diffuse.png",14);
    }
}
