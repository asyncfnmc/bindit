package dev.bindit;

import java.util.ArrayList;
import java.util.List;

public final class Binding {
    public String name = "New binding";
    public List<Integer> keys = new ArrayList<>();
    public String action = "";
    public String island = "";
    public boolean enabled = true;
    public transient boolean wasDown;

    public Binding copy() {
        Binding b = new Binding(); b.name = name; b.keys = new ArrayList<>(keys); b.action = action; b.island = island; b.enabled = enabled; return b;
    }
}
