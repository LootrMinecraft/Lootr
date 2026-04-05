package noobanidus.mods.lootr.common.impl.command;

import com.google.auto.service.AutoService;
import net.minecraft.world.level.block.Block;
import noobanidus.mods.lootr.common.api.command.ILootrCommandExtension;
import noobanidus.mods.lootr.common.api.LootrRegistry;

@AutoService(ILootrCommandExtension.class)
public class BarrelCommandType implements ILootrCommandExtension {
  @Override
  public Block getBlock() {
    return LootrRegistry.getBarrelBlock();
  }

  @Override
  public String getId() {
    return "barrel";
  }
}
