package noobanidus.mods.lootr.api;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.resources.ResourceLocation;

@FunctionalInterface
public interface LootFiller {
  void fillWithLoot(Player player, Container inventory, ResourceLocation table, long seed);
}
