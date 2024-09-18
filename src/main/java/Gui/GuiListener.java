package Gui;

import Gui.startmenu.Button;
import Gui.startmenu.GuiItem;
import Input.MouseInput;
import events.EventBus;
import events.GUIClickEvent;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;

public class GuiListener {
    private MouseInput mouseInput;
    private static EventBus guiEventBus;
    private static ArrayDeque<GuiItem> delQue;
    private static ArrayList<GuiItem> guiItems;
    public GuiListener(MouseInput mouseInput){
        this.mouseInput = mouseInput;
        guiEventBus = new EventBus();
        guiItems = new ArrayList<>();
        delQue = new ArrayDeque<>();
        guiEventBus.subscribe(GUIClickEvent.class, this::onButtonClick);
    }

    public void onButtonClick(GUIClickEvent event){
        if(event.getGuiItem() instanceof Button){
            Button button = (Button) event.getGuiItem();
            button.getGuiTexture().setColour(new Vector4f(0,0,0,0));
        }
    }

    public static EventBus getGuiEventBus() {
        return guiEventBus;
    }

    public static void addNewItem(GuiItem item){
        guiItems.add(item);
    }

    public static void removeItem(GuiItem item){
        delQue.add(item);
    }

    public void listen(){
        Vector2d mPos = mouseInput.getCurrentPos();
        Iterator<GuiItem> itemIterator = guiItems.iterator();
        while (itemIterator.hasNext()){
            GuiItem guiItem = itemIterator.next();
            if(!delQue.isEmpty()){
                if(delQue.getFirst().equals(guiItem)){
                    delQue.removeFirst();
                    itemIterator.remove();
                    continue;
                }
                
            }
            Vector2d itemPos = guiItem.getPos();
            Vector2f itemSize = guiItem.getSize();
            if(mPos.x > itemPos.x - itemSize.x/2 && mPos.x < itemPos.x + itemSize.x / 2
                    && mPos.y > itemPos.y - itemSize.y && mPos.y < itemPos.y+itemSize.y / 2){
                guiItem.setHoveredOver(true);
                if(mouseInput.isLeftButtonPressed()){
                    guiEventBus.send(new GUIClickEvent(guiItem, ClickType.LEFT));
                }
                else if(mouseInput.isRightButtonPressed()){
                    guiEventBus.send(new GUIClickEvent(guiItem, ClickType.RIGHT));
                }
            }
            else {
                guiItem.setHoveredOver(false);
            }
        }
    }
}
