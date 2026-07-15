package dev.bindit;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import java.util.ArrayList;
import java.util.List;

public final class BindItScreen extends Screen {
    private final Screen parent;
    private int page;
    private static final int ROWS = 6;
    public BindItScreen(Screen parent) { super(Component.literal("Bind It")); this.parent = parent; }
    @Override protected void init() {
        clearWidgets();
        int panel = Math.min(620, width - 32), left = (width-panel)/2;
        int popupHeight = Math.min(height-32, ROWS*34+94), popupTop = (height-popupHeight)/2, top = popupTop+40;
        List<Binding> list = BindingStore.BINDINGS;
        int start = page * ROWS;
        for (int row=0; row<ROWS && start+row<list.size(); row++) {
            int index=start+row, y=top+row*34; Binding b=list.get(index);
            Button main = Button.builder(Component.literal((b.enabled ? "[x] " : "[ ] ") + b.name + "  |  " + KeyNames.combo(b.keys)), x -> { b.enabled=!b.enabled; BindingStore.save(); rebuildWidgets(); }).bounds(left+10,y,panel-166,26).build();
            String details = b.action + ((b.island == null || b.island.isBlank()) ? "" : "\nIsland | " + b.island);
            main.setTooltip(Tooltip.create(Component.literal(details))); addRenderableWidget(main);
            addRenderableWidget(Button.builder(Component.literal("EDIT"), x -> minecraft.setScreen(new BindingEditorScreen(this,b))).bounds(left+panel-148,y,66,26).build());
            addRenderableWidget(Button.builder(Component.literal("DEL"), x -> { list.remove(index); BindingStore.save(); if (page>0 && page*ROWS>=list.size()) page--; rebuildWidgets(); }).bounds(left+panel-74,y,64,26).build());
        }
        int bottom=popupTop+popupHeight-34;
        addRenderableWidget(Button.builder(Component.literal("+ ADD BINDING"), x -> minecraft.setScreen(new BindingEditorScreen(this,null))).bounds(left+10,bottom,150,24).build());
        if(page>0) addRenderableWidget(Button.builder(Component.literal("< PREV"),x->{page--;rebuildWidgets();}).bounds(left+panel-242,bottom,70,24).build());
        if((page+1)*ROWS<list.size()) addRenderableWidget(Button.builder(Component.literal("NEXT >"),x->{page++;rebuildWidgets();}).bounds(left+panel-164,bottom,70,24).build());
        addRenderableWidget(Button.builder(Component.literal("DONE"),x->onClose()).bounds(left+panel-84,bottom,74,24).build());
    }
    @Override public void extractRenderState(GuiGraphicsExtractor g,int mx,int my,float d) {
        int panel=Math.min(620,width-32), left=(width-panel)/2;
        int popupHeight=Math.min(height-32, ROWS*34+94), popupTop=(height-popupHeight)/2;
        g.fill(0,0,width,height,0x88000000);
        g.fill(left,popupTop,left+panel,popupTop+popupHeight,0xFF090909);
        g.outline(left,popupTop,panel,popupHeight,0xFF555555);
        g.text(font,"BIND IT",left+12,popupTop+13,0xFFFFFFFF);
        g.text(font,BindingStore.BINDINGS.size()+" BINDS",left+panel-12-font.width(BindingStore.BINDINGS.size()+" BINDS"),popupTop+13,0xFF999999);
        g.horizontalLine(left+12,left+panel-13,popupTop+31,0xFF444444);
        if(BindingStore.BINDINGS.isEmpty()) g.centeredText(font,"No binds yet. Add one to get started.",width/2,popupTop+76,0xFFAAAAAA);
        super.extractRenderState(g,mx,my,d);
    }
    @Override public void onClose(){ minecraft.setScreen(parent); }
}
