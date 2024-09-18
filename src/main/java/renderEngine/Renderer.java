package renderEngine;



import org.lwjgl.opengl.GL30;

public abstract class Renderer {
    protected Camera camera;

    public Renderer(Camera camera){
        this.camera = camera;
    }
}
