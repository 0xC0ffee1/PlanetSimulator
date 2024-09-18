package events;

import Gui.ClickType;
import Gui.startmenu.GuiItem;


public class GUIClickEvent extends Event {
    private GuiItem guiItem;
    private ClickType clickType;
    public GUIClickEvent(GuiItem guiItem, ClickType clickType){
        this.guiItem = guiItem;
        this.clickType = clickType;
    }
    public GuiItem getGuiItem() {
        return guiItem;
    }
    public ClickType getClickType() {
        return clickType;
    }
}
