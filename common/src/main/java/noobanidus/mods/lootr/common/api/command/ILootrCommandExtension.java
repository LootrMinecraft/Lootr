package noobanidus.mods.lootr.common.api.command;

import net.minecraft.world.level.block.Block;

public interface ILootrCommandExtension {
  Block getBlock();
  String getId();
}
