package Textures;

import Misc.WorldConstants;

public class MetalTextureArray extends TextureArray{
    public MetalTextureArray() {
        super(1024, WorldConstants.TEXTURE_COUNT, "terrain/");
    }

    @Override
    public void loadLayers() {
        loadToLayer("metalplate_metal.jpg", 5);
    }
}
