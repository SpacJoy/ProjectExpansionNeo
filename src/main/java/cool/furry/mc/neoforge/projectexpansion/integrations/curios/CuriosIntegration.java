package cool.furry.mc.neoforge.projectexpansion.integrations.curios;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import moze_intel.projecte.api.item_handlers.IItemHandlerModifiable;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.entity.player.Player;

/** Access to the dedicated tablet slot on Fabric. */
public final class CuriosIntegration {
    public static final String MOD_ID = "trinkets";
    public static final String TABLET_SLOT_ID = "transmutation_tablet";
    public static boolean modLoaded() { return FabricLoader.getInstance().isModLoaded(MOD_ID); }
    public static Optional<IItemHandlerModifiable> getCuriosInventory(Player player) {
        return modLoaded() ? Integrator.inventory(player) : Optional.empty();
    }
    private static final class Integrator {
        static Optional<IItemHandlerModifiable> inventory(Player player) {
            //26.3 note: Trinkets was rewritten by Patbox. The api package moved from dev.emi.trinkets.api to
            //eu.pb4.trinkets.api, TrinketComponent is now TrinketAttachment (reached through TrinketsApi#getAttachment),
            //and the inventories are already flattened into a single map, so there is no per-group lookup any more.
            eu.pb4.trinkets.api.TrinketAttachment attachment = eu.pb4.trinkets.api.TrinketsApi.getAttachment(player);
            if (attachment == null) {
                return Optional.empty();
            }
            List<eu.pb4.trinkets.api.TrinketInventory> inventories = new ArrayList<>(attachment.getInventories().values());
            return inventories.isEmpty() ? Optional.empty()
                    : Optional.of(new moze_intel.projecte.integration.trinkets.TrinketItemHandler(inventories));
        }
    }
}
