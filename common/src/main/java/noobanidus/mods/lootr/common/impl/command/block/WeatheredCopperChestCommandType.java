package noobanidus.mods.lootr.common.impl.command.block;

import com.google.auto.service.AutoService;
import net.minecraft.world.level.block.Block;
import noobanidus.mods.lootr.common.api.LootrRegistry;
import noobanidus.mods.lootr.common.api.interfaces.command.ILootrCommandBlockExtension;

@AutoService(ILootrCommandBlockExtension.class)
public class WeatheredCopperChestCommandType implements ILootrCommandBlockExtension {
  @Override
  public Block getBlock() {
    return LootrRegistry.getWeatheredCopperChestBlock();
  }

  @Override
  public String getId() {
    return "weathered_copper_chest";
  }
}
