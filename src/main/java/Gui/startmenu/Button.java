package Gui.startmenu;

import Gui.GuiRenderer;
import Misc.WorldConstants;
import TextMesh.GUIText;
import TextRendering.TextMaster;
import Textures.GuiTexture;
import Textures.Texture;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class Button extends GuiItem{
    private Texture texture;
    private GuiTexture guiTexture;
    private String text;
    private GUIText guiText;
    private float x;
    private float y;

    public Button(String text, float x, float y, float sizeX, float sizeY) {
        super(sizeX,sizeY,x,y);
        this.texture = new Texture(WorldConstants.RES_DIR + "gui/button.png");
        this.text = text;
        this.x = x;
        this.y = y;
        this.guiTexture = new GuiTexture(texture.getID(), new Vector2f(x,y), getScale());
        this.guiText = new GUIText(text, 1, StartMenu.getFont(), new Vector2f((x + 0.5f) * 0.5f, (1f - y)/2 - 0.017f), 0.5f, true);
        guiText.setColour(1,1,1);
    }

    public String getText() {
        return text;
    }

    @Override
    public void setHoveredOver(boolean hoveredOver) {
        isHoveredOver = hoveredOver;
        if(hoveredOver){
            guiTexture.setColour(new Vector4f( 	8/255f, 46/255f, 208/255f,0.2f));
        }
        else {
            guiTexture.setColour(new Vector4f(0,0,0,0));
        }
    }

    @Override
    public GuiTexture getGuiTexture() {
        return guiTexture;
    }
    public void destroy(GuiRenderer guiRenderer){
        guiRenderer.removeGui(guiTexture);
        TextMaster.removeText(guiText);
    }
}
