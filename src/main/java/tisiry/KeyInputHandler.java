package tisiry;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeyInputHandler {
    public static KeyBinding moveHudKey;

    public static void register() {
        // Оставляем только одну кнопку K для открытия меню настроек
        moveHudKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Настройка HUD",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                "VupsPing HUD"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && moveHudKey.wasPressed()) {
                client.setScreen(new MoveHudScreen(null));
            }
        });
    }
}
