package tisiry;

import net.fabricmc.api.ClientModInitializer;

public class VupsbetterClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // 1. Загружаем координаты HUD и масштаб из файла vupping.json
        ModConfig.load();

        // 2. Включаем отображение FPS, TPS, пинга и координат на экране
        HudRenderer.register();

        // 3. Регистрируем кнопку "K" для входа в режим перетаскивания
        KeyInputHandler.register();
    }
}
