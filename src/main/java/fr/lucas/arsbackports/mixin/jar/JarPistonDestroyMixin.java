package fr.lucas.arsbackports.mixin.jar;

import com.hollingsworth.arsnouveau.common.block.MobJar;
import com.hollingsworth.arsnouveau.common.block.SourceJar;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Backport of 1.21.1 "push source/containment jars w/ piston to break & dispense to place" (#1661), piston part:
 * pistons break Source Jars (and Creative Source Jars, which extend them) and Containment Jars instead of being
 * blocked by their block entity. Implements Forge's IForgeBlock#getPistonPushReaction, which none of these classes
 * (nor ModBlock/SourceBlock/TickableModBlock) override in 4.12.7. The dispenser part is in ModDispenserBehaviors.
 */
@Mixin({SourceJar.class, MobJar.class})
public abstract class JarPistonDestroyMixin {

    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }
}
