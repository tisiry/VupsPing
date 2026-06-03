package tisiry;

import net.fabricmc.api.ClientModInitializer;

public class VupsbetterClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModConfig.load();
        HudRenderer.register();
        KeyInputHandler.register();
    }
}
