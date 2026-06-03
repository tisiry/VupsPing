package tisiry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class ModConfig {
    private static final File FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "vupsping.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static float hudScale = 1.0f;

    // ИСПРАВЛЕНО: Более удобные дефолтные позиции (сдвинуты по горизонтали, чтобы не слипались при первом запуске)
    public static int xyzX = 10, xyzY = 10;
    public static int fpsX = 110, fpsY = 10;
    public static int pingX = 210, pingY = 10;
    public static int tpsX = 310, tpsY = 10;

    public static boolean showXyz = true;
    public static boolean showFps = true;
    public static boolean showPing = true;
    public static boolean showTps = true;

    public static void load() {
        if (!FILE.exists()) {
            save();
            return;
        }
        try (FileReader reader = new FileReader(FILE)) {
            ConfigData data = GSON.fromJson(reader, ConfigData.class);
            if (data != null) {
                hudScale = data.hudScale;
                xyzX = data.xyzX; xyzY = data.xyzY;
                fpsX = data.fpsX; fpsY = data.fpsY;
                pingX = data.pingX; pingY = data.pingY;
                tpsX = data.tpsX; tpsY = data.tpsY;
                showXyz = data.showXyz;
                showFps = data.showFps;
                showPing = data.showPing;
                showTps = data.showTps;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try {
            // Защита: создаем родительские папки, если конфигурационной директории ещё нет
            File parent = FILE.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            try (FileWriter writer = new FileWriter(FILE)) {
                ConfigData data = new ConfigData();
                data.hudScale = hudScale;
                data.xyzX = xyzX; data.xyzY = xyzY;
                data.fpsX = fpsX; data.fpsY = fpsY;
                data.pingX = pingX; data.pingY = pingY;
                data.tpsX = tpsX; data.tpsY = tpsY;
                data.showXyz = showXyz;
                data.showFps = showFps;
                data.showPing = showPing;
                data.showTps = showTps;
                GSON.toJson(data, writer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class ConfigData {
        float hudScale = 1.0f;
        // Изменяем значения и во внутреннем классе сериализатора GSON
        int xyzX = 10, xyzY = 10;
        int fpsX = 110, fpsY = 10;
        int pingX = 210, pingY = 10;
        int tpsX = 310, tpsY = 10;
        boolean showXyz = true;
        boolean showFps = true;
        boolean showPing = true;
        boolean showTps = true;
    }
}
