package Gui;

import org.joml.Matrix4f;
import shaders.ShaderProgram;

public class GuiShader extends ShaderProgram {
    private static final String VERTEX_FILE = "gui/guiVertexShader.txt";
    private static final String FRAGMENT_FILE = "gui/guiFragmentShader.txt";

    public GuiShader() throws Exception{
        super(VERTEX_FILE, FRAGMENT_FILE);
        createUniform("transformationMatrix");
        createUniform("shadeColour");
    }

    public void loadTransformation(Matrix4f matrix){
        setUniform("transformationMatrix", matrix);
    }


}
