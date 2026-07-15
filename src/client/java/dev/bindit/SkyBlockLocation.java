package dev.bindit;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Reads Hypixel's top-level "Area:" value from the player tab list. */
public final class SkyBlockLocation {
    private static final Pattern AREA = Pattern.compile("^\\s*Area\\s*:\\s*(.+?)\\s*$", Pattern.CASE_INSENSITIVE);
    private SkyBlockLocation() {}

    public static String current(Minecraft minecraft) {
        if (minecraft.getConnection() == null) return "";
        for (var playerInfo : minecraft.getConnection().getListedOnlinePlayers()) {
            Component display = playerInfo.getTabListDisplayName();
            String text = display != null ? display.getString() : playerInfo.getProfile().name();
            Matcher matcher = AREA.matcher(stripSymbols(text));
            if (matcher.matches()) return matcher.group(1).trim();
        }
        return "";
    }

    public static boolean matches(String currentIsland, String requiredIsland) {
        if (requiredIsland == null || requiredIsland.isBlank()) return true;
        return normalize(currentIsland).equals(normalize(requiredIsland));
    }

    private static String normalize(String value) {
        return stripSymbols(value).trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
    }

    private static String stripSymbols(String value) {
        return value.replace("⏣", "").replace("♨", "").trim();
    }
}
