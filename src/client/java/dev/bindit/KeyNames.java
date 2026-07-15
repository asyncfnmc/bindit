package dev.bindit;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.List;
import java.util.stream.Collectors;
public final class KeyNames {
    private KeyNames() {}
    public static String name(int key) {
        String n = InputConstants.Type.KEYSYM.getOrCreate(key).getDisplayName().getString();
        return n.length() == 1 ? n.toUpperCase() : n.replace("Left ", "L ").replace("Right ", "R ");
    }
    public static String combo(List<Integer> keys) { return keys.isEmpty() ? "UNBOUND" : keys.stream().map(KeyNames::name).collect(Collectors.joining(" + ")); }
}
