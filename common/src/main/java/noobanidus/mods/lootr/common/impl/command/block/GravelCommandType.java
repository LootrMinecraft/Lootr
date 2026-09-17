package noobanidus.mods.lootr.common.impl.command.block;

import com.google.auto.service.AutoService;
import net.minecraft.world.level.block.Block;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;
import noobanidus.mods.lootr.common.api.command.ILootrCommandBlockExtension;

@AutoService(ILootrCommandBlockExtension.class)
public class GravelCommandType implements ILootrCommandBlockExtension {
  @Override
  public Block getBlock() {
    return LootrRegistry.getSuspiciousGravelBlock();
  }

  @Override
  public String getId() {
    return "gravel";
  }
}
