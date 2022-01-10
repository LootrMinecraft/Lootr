package noobanidus.mods.lootr.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;

@FunctionalInterface
public interface LootFiller {
  void fillWithLoot(Player player, Container inventory, ResourceLocation table, long seed);
}
