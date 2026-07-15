package dev.bindit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class BindingStore {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("bindit.json");
    public static final List<Binding> BINDINGS = new ArrayList<>();
    private BindingStore() {}
    public static void load() {
        BINDINGS.clear();
        if (!Files.exists(FILE)) return;
        try (Reader r = Files.newBufferedReader(FILE)) {
            List<Binding> values = GSON.fromJson(r, new TypeToken<List<Binding>>(){}.getType());
            if (values != null) BINDINGS.addAll(values);
        } catch (Exception e) { BindItClient.LOGGER.error("Could not read {}", FILE, e); }
    }
    public static void save() {
        try { Files.createDirectories(FILE.getParent()); try (Writer w = Files.newBufferedWriter(FILE)) { GSON.toJson(BINDINGS, w); } }
        catch (Exception e) { BindItClient.LOGGER.error("Could not write {}", FILE, e); }
    }
}
