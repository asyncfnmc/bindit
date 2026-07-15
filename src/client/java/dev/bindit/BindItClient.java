package dev.bindit;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.brigadier.Command;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BindItClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("bindit");
    @Override public void onInitializeClient() {
        BindingStore.load();
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, access) -> dispatcher.register(
            net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal("bindit").executes(ctx -> {
                Minecraft.getInstance().execute(() -> Minecraft.getInstance().setScreen(new BindItScreen(null)));
                return Command.SINGLE_SUCCESS;
            })));
        ClientTickEvents.END_CLIENT_TICK.register(this::tick);
    }
    private void tick(Minecraft mc) {
        if (mc.player == null || mc.getConnection() == null || mc.screen != null) { BindingStore.BINDINGS.forEach(b -> b.wasDown = false); return; }
        var window = mc.getWindow();
        var bindings = BindingStore.BINDINGS;
        boolean hasIslandConditions = bindings.stream().anyMatch(b -> b.island != null && !b.island.isBlank());
        String currentIsland = hasIslandConditions ? SkyBlockLocation.current(mc) : "";
        boolean[] down = new boolean[bindings.size()];

        for (int i = 0; i < bindings.size(); i++) {
            Binding b = bindings.get(i);
            down[i] = b.enabled && !b.keys.isEmpty()
                && SkyBlockLocation.matches(currentIsland, b.island)
                && b.keys.stream().allMatch(k -> InputConstants.isKeyDown(window, k));
        }

        for (int i = 0; i < bindings.size(); i++) {
            Binding b = bindings.get(i);
            boolean shadowedByLongerCombo = false;
            if (down[i]) {
                for (int j = 0; j < bindings.size(); j++) {
                    if (i != j && down[j]
                        && bindings.get(j).keys.size() > b.keys.size()
                        && bindings.get(j).keys.containsAll(b.keys)) {
                        shadowedByLongerCombo = true;
                        break;
                    }
                }
            }

            if (down[i] && !shadowedByLongerCombo && !b.wasDown && !b.action.isBlank()) {
                String action = b.action.trim();
                if (action.startsWith("/")) mc.getConnection().sendCommand(action.substring(1));
                else mc.getConnection().sendChat(action);
            }
            b.wasDown = down[i];
        }
    }
}
