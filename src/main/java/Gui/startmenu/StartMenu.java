package Gui.startmenu;

import Gui.GuiListener;
import Gui.GuiRenderer;
import Misc.WorldConstants;
import TextMesh.FontType;
import TextMesh.GUIText;
import TextRendering.TextMaster;
import Textures.GuiTexture;
import Textures.Texture;
import events.GUIClickEvent;
import org.joml.Vector2f;
import renderEngine.Window;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StartMenu extends PauseMenu{
    private GuiRenderer guiRenderer;
    private static FontType fontType;
    private List<Button> buttons;
    private GUIText title;
    private GuiTexture background;
    public StartMenu(GuiRenderer guiRenderer){
        fontType = new FontType(new Texture(WorldConstants.RES_DIR + "fonts/voxelfont1.png").getID(), new File(WorldConstants.RES_DIR + "fonts/voxelfont.fnt"));
        this.guiRenderer = guiRenderer;
        buttons = new ArrayList<>();
        init();
        GuiListener.getGuiEventBus().subscribe(GUIClickEvent.class, this::onButtonClick);
    }

    public void onButtonClick(GUIClickEvent event){
        if(event.getGuiItem() instanceof Button){
            Button button = (Button) event.getGuiItem();
            if(button.getText().equals("Play")){
                canContinue = true;
                destroy();
            }
            else if(button.getText().equals("Quit")){
                System.exit(0);
            }
        }
    }

    public void destroy(){
        buttons.forEach(button -> {
            button.destroy(guiRenderer);
            GuiListener.removeItem(button);
        });
        guiRenderer.removeGui(background);
        TextMaster.removeText(title);
        Window.hideCursor(true);
    }

    public static FontType getFont(){
        return fontType;
    }

    public void addButton(Button button){
        guiRenderer.addGui(button.getGuiTexture());
        GuiListener.addNewItem(button);
        buttons.add(button);
    }

    public void init(){
        title = new GUIText("Random planet game", 3f, fontType, new Vector2f(0.02f,0.2f), 1f, true);
        title.setColour(1,0,1);
        background = new GuiTexture(new Texture(WorldConstants.RES_DIR +"gui/background.png").getID(), new Vector2f(0, 0f), new Vector2f(1, 1));
        guiRenderer.addGui(background);
        addButton(new Button("Play", 0f, 0.25f, 400, 40));
        addButton(new Button("Test 1", -0.11f, 0.15f, 190, 40));
        addButton(new Button("Test 2", 0.11f, 0.15f, 190, 40));
        addButton(new Button("Test 3", 0, 0.05f, 400, 40));
        addButton(new Button("Quit", 0f, -0.1f, 400, 40));
    }
}
