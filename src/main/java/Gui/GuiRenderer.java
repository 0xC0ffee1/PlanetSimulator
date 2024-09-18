package Gui;

import Textures.GuiTexture;
import models.Mesh;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import renderEngine.Loader;
import renderEngine.Transformation;

import java.util.ArrayList;
import java.util.List;

public class GuiRenderer {
    private final Mesh quad;
    private GuiShader guiShader;
    private List<GuiTexture> guis;
    public GuiRenderer(Loader loader){
        float[] positions = {-1,1,-1,-1,1,1,1,-1};
        quad = loader.loadToVAO(positions, 2);
        guis = new ArrayList<>();
        try {
            guiShader = new GuiShader();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void addGui(GuiTexture gui){
        if(!guis.contains(gui)){
            guis.add(gui);
        }
    }
    public void removeGui(GuiTexture gui){
        guis.remove(gui);
    }

    public void render(){
        //@TODO planeclip
        guiShader.start();
        GL30.glBindVertexArray(quad.getVaoID());
        GL30.glEnable(GL30.GL_BLEND);
        GL30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        GL30.glDisable(GL30.GL_DEPTH_TEST);
        for(GuiTexture gui : guis){
            GL30.glActiveTexture(GL30.GL_TEXTURE0);
            GL30.glBindTexture(GL11.GL_TEXTURE_2D, gui.getTextureID());
            Matrix4f tMatrix = Transformation.getTransformation(gui.getPosition(), gui.getScale());
            guiShader.loadTransformation(tMatrix);
            guiShader.setUniform("shadeColour", gui.getColour());
            GL30.glDrawArrays(GL30.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
        }
        GL30.glEnable(GL30.GL_DEPTH_TEST);
        GL30.glDisable(GL30.GL_BLEND);
        GL30.glBindVertexArray(0);
        guiShader.unbind();
    }
}
