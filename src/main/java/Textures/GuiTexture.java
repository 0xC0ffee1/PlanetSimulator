package Textures;

import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL30;

public class GuiTexture {
    private int textureID;
    private Vector2f position;
    private Vector2f scale;
    private Vector4f colour;

    public GuiTexture(int textureID, Vector2f position, Vector2f scale) {
        this.textureID = textureID;
        this.position = position;
        this.scale = scale;
        this.colour = new Vector4f(0,0,0,0);
    }


    public void setColour(Vector4f colour){
        this.colour = colour;
    }

    public Vector4f getColour() {
        return colour;
    }

    public void setPosition(Vector2f position) {
        this.position = position;
    }

    public void setScale(Vector2f scale) {
        this.scale = scale;
    }

    public int getTextureID() {
        return textureID;
    }

    public Vector2f getPosition() {
        return position;
    }

    public Vector2f getScale() {
        return scale;
    }
}
