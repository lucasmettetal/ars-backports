package fr.lucas.arsbackports.mixin.ritual;

import com.hollingsworth.arsnouveau.common.ritual.RitualCloudshaper;
import com.hollingsworth.arsnouveau.common.ritual.RitualMoonfall;
import com.hollingsworth.arsnouveau.common.ritual.RitualSunrise;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Backport of 1.21.1 "allow weather and time rituals to work in all dimensions" (#2101, works around MC-188578):
 * weather and time are global and only change when set on the Overworld, so the Cloudshaper, Moonfall and Sunrise
 * rituals act on the Overworld whatever dimension they run in. 1.21.1 reassigns the ritual's ServerLevel local to
 * {@code getServer().overworld()}; this modifies the same (only) ServerLevel local when it is stored.
 */
@Mixin({RitualCloudshaper.class, RitualMoonfall.class, RitualSunrise.class})
public abstract class GlobalWeatherTimeMixin {

    @ModifyVariable(method = "tick", at = @At("STORE"), ordinal = 0, remap = false)
    private ServerLevel ars_backports$useOverworld(ServerLevel level) {
        return level.getServer().overworld();
    }
}
