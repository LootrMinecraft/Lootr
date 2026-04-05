package noobanidus.mods.lootr.common.impl.command;

import com.google.auto.service.AutoService;
import net.minecraft.world.level.block.Block;
import noobanidus.mods.lootr.common.api.command.ILootrCommandExtension;
import noobanidus.mods.lootr.common.api.LootrRegistry;

@AutoService(ILootrCommandExtension.class)
public class TrappedChestCommandType implements ILootrCommandExtension {
  @Override
  public Block getBlock() {
    return LootrRegistry.getTrappedChestBlock();
  }

  @Override
  public String getId() {
    return "trapped_chest";
  }
}
