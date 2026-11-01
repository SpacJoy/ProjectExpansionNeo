package cool.furry.mc.neoforge.projectexpansion.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public class Keybinds {
    public static boolean REGISTERED = false;
    public static KeyMapping OPEN_TRANSMUTATION_TABLET;
    private static KeyMapping.Category CATEGORY;

    public static void register() {
        if (REGISTERED) {
            return;
        }
        //26.3 replaced the key binding helper package and reshaped KeyMapping around InputConstants keys and a
        // Category record, so the binding is built off those instead of the raw GLFW constants
        //26.3 also throws when the same Category id is registered twice, so it is created once and kept
        if (CATEGORY == null) {
            CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("projecte", "projecte"));
        }
        OPEN_TRANSMUTATION_TABLET = new KeyMapping(
                "key.projecte.curios.open_transmutation_tablet",
                InputConstants.Type.KEYBOARD,
                InputConstants.KEY_K,
                CATEGORY
        );
        KeyMappingHelper.registerKeyMapping(OPEN_TRANSMUTATION_TABLET);
        REGISTERED = true;
    }
}
