package noobanidus.mods.lootr.common.api.interfaces.command;

import net.minecraft.world.level.block.Block;

public interface ILootrCommandBlockExtension extends ILootrCommandExtension {
  Block getBlock();
}
