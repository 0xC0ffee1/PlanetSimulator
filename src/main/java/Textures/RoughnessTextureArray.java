package Textures;


import Misc.WorldConstants;

public class RoughnessTextureArray extends TextureArray{

    public RoughnessTextureArray() {
        super(1024, WorldConstants.TEXTURE_COUNT, "terrain/");
    }

    @Override
    public void loadLayers() {
        loadToLayer("grass_roughness.jpg", 1);
        loadToLayer("rock_roughness.jpg", 2);
        loadToLayer("rocks3_roughness.jpg", 3);
        loadToLayer("obsidian_roughness.jpg", 4);
        loadToLayer("metalplate_roughness.jpg", 5);
        loadToLayer("glass_roughness.jpg", 6);
        loadToLayer("ground_roughness.jpg", 7);
        loadToLayer("cliff_roughness.jpg", 8);
        loadToLayer("mud_roughness.jpg", 9);
        loadToLayer("wetrocks2_roughness.jpg", 10);
        loadToLayer("snow_roughness.jpg", 11);
        loadToLayer("quartz_roughness.jpg", 12);
        loadToLayer("water_roughness.jpg",13);
    }
}
