package noobanidus.mods.lootr.common.impl.command;

import com.google.auto.service.AutoService;
import net.minecraft.world.level.block.Block;
import noobanidus.mods.lootr.common.api.command.ILootrCommandExtension;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;

@AutoService(ILootrCommandExtension.class)
public class GravelCommandType implements ILootrCommandExtension {
  @Override
  public Block getBlock() {
    return LootrRegistry.getSuspiciousGravelBlock();
  }

  @Override
  public String getId() {
    return "gravel";
  }
}
