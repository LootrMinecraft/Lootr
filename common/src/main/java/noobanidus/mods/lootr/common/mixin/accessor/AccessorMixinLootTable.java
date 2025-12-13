package noobanidus.mods.lootr.common.mixin.accessor;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;

@Mixin(LootTable.class)
public interface AccessorMixinLootTable {
  @Accessor("randomSequence")
  Optional<ResourceLocation> getRandomSequence();
}
