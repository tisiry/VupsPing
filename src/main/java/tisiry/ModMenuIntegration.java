package tisiry;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        // Указываем Mod Menu открывать ваше окно перемещения плашек
        return parent -> new MoveHudScreen(parent);
    }
}
