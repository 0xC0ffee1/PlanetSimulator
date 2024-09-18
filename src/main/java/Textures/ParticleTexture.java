package Textures;

public class ParticleTexture {
    private int textureID;
    private int rows;

    private boolean additive;

    public ParticleTexture(int textureID, int rows, boolean additive) {
        this.textureID = textureID;
        this.rows = rows;
        this.additive = additive;
    }

    public boolean isAdditive() {
        return additive;
    }

    public int getTextureID() {
        return textureID;
    }

    public int getRows() {
        return rows;
    }
}
