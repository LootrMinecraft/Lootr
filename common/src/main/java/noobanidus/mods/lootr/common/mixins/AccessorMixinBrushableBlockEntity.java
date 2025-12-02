package noobanidus.mods.lootr.common.mixins;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BrushableBlockEntity.class)
public interface AccessorMixinBrushableBlockEntity {
  @Accessor("lootTable")
  ResourceKey<LootTable> lootr$getLootTable();

  @Accessor("lootTableSeed")
  long lootr$getLootTableSeed();
}
