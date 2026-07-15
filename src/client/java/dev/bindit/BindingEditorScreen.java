package dev.bindit;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import java.util.ArrayList;

public final class BindingEditorScreen extends Screen {
    private static final int POPUP_HEIGHT = 278;
    private final Screen parent;
    private final Binding original;
    private final Binding draft;
    private EditBox name, action, island;
    private Button capture;
    private boolean capturing;

    BindingEditorScreen(Screen parent, Binding binding) {
        super(Component.literal(binding == null ? "Add bind" : "Edit bind"));
        this.parent = parent;
        this.original = binding;
        this.draft = binding == null ? new Binding() : binding.copy();
    }

    @Override protected void init() {
        clearWidgets();
        int w = Math.min(500, width-40), x = (width-w)/2, top = (height-POPUP_HEIGHT)/2;
        name = new EditBox(font,x+12,top+42,w-24,24,Component.literal("Bind name"));
        name.setMaxLength(64); name.setValue(draft.name); addRenderableWidget(name);
        capture = addRenderableWidget(Button.builder(Component.literal(KeyNames.combo(draft.keys)), b -> {
            capturing=true; draft.keys.clear(); b.setMessage(Component.literal("PRESS KEYS | ENTER TO FINISH"));
        }).bounds(x+12,top+86,w-24,26).build());
        action = new EditBox(font,x+12,top+132,w-24,24,Component.literal("Message or /command"));
        action.setMaxLength(256); action.setValue(draft.action); addRenderableWidget(action);
        island = new EditBox(font,x+12,top+178,w-134,24,Component.literal("Any island"));
        island.setMaxLength(64); island.setValue(draft.island == null ? "" : draft.island); addRenderableWidget(island);
        addRenderableWidget(Button.builder(Component.literal("USE CURRENT"), b -> {
            String current = SkyBlockLocation.current(minecraft);
            if (!current.isBlank()) island.setValue(current);
        }).bounds(x+w-116,top+178,104,24).build());
        addRenderableWidget(Button.builder(Component.literal("SAVE"),b->save()).bounds(x+12,top+230,(w-30)/2,26).build());
        addRenderableWidget(Button.builder(Component.literal("CANCEL"),b->onClose()).bounds(x+18+(w-30)/2,top+230,(w-30)/2,26).build());
        setInitialFocus(name);
    }

    @Override public boolean keyPressed(KeyEvent event) {
        if (capturing) {
            int key = event.key();
            if (key == GLFW.GLFW_KEY_ESCAPE) { capturing=false; capture.setMessage(Component.literal(KeyNames.combo(draft.keys))); return true; }
            if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) { capturing=false; capture.setMessage(Component.literal(KeyNames.combo(draft.keys))); return true; }
            if (!draft.keys.contains(key)) draft.keys.add(key);
            capture.setMessage(Component.literal(KeyNames.combo(draft.keys)+"  [ENTER]"));
            return true;
        }
        return super.keyPressed(event);
    }

    private void save() {
        draft.name = name.getValue().isBlank() ? "Unnamed bind" : name.getValue().trim();
        draft.action = action.getValue().trim();
        draft.island = island.getValue().trim();
        if (original == null) BindingStore.BINDINGS.add(draft);
        else {
            original.name=draft.name; original.keys=new ArrayList<>(draft.keys); original.action=draft.action;
            original.island=draft.island; original.enabled=draft.enabled;
        }
        BindingStore.save(); minecraft.setScreen(parent);
    }

    @Override public void extractRenderState(GuiGraphicsExtractor g,int mx,int my,float d) {
        int w=Math.min(500,width-40),x=(width-w)/2,top=(height-POPUP_HEIGHT)/2;
        g.fill(0,0,width,height,0x88000000); g.fill(x,top,x+w,top+POPUP_HEIGHT,0xFF090909); g.outline(x,top,w,POPUP_HEIGHT,0xFF555555);
        g.text(font,original==null?"NEW BIND":"EDIT BIND",x+12,top+13,0xFFFFFFFF); g.horizontalLine(x+12,x+w-13,top+29,0xFF444444);
        g.text(font,"NAME",x+12,top+31,0xFFAAAAAA); g.text(font,"SHORTCUT",x+12,top+74,0xFFAAAAAA);
        g.text(font,"ACTION | Prefix commands with /",x+12,top+120,0xFFAAAAAA);
        g.text(font,"ISLAND | Optional, uses Hypixel tab list area",x+12,top+166,0xFFAAAAAA);
        super.extractRenderState(g,mx,my,d);
    }

    @Override public void onClose() { minecraft.setScreen(parent); }
}
