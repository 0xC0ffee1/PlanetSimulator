package Textures;

import Misc.WorldConstants;

public class AmbientOcclusionTextureArray extends TextureArray {
    public AmbientOcclusionTextureArray() {
        super(1024, WorldConstants.TEXTURE_COUNT, "terrain/");
    }

    @Override
    public void loadLayers() {
        loadToLayer("grass_occlusion.jpg", 1);
        loadToLayer("rocks_occlusion.jpg", 2);
        loadToLayer("rocks3_occlusion.jpg", 3);
        loadToLayer("obsidian_occlusion.jpg", 4);
        loadToLayer("metalplate_occlusion.jpg", 5);
        loadToLayer("glass_occlusion.jpg", 6);
        loadToLayer("ground_occlusion.jpg", 7);
        loadToLayer("cliff_occlusion.jpg", 8);
        loadToLayer("mud_occlusion.jpg", 9);
        loadToLayer("wetrocks2_occlusion.jpg", 10);
        loadToLayer("snow_occlusion.jpg", 11);
        loadToLayer("quartz_occlusion.jpg", 12);
        loadToLayer("water_occlusion.jpg", 13);
    }
}
