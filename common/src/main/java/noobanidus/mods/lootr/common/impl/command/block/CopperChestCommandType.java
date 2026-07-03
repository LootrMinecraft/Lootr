package noobanidus.mods.lootr.common.impl.command.block;

import com.google.auto.service.AutoService;
import net.minecraft.world.level.block.Block;
import noobanidus.mods.lootr.common.api.LootrRegistry;
import noobanidus.mods.lootr.common.api.interfaces.command.ILootrCommandBlockExtension;

@AutoService(ILootrCommandBlockExtension.class)
public class CopperChestCommandType implements ILootrCommandBlockExtension {
  @Override
  public Block getBlock() {
    return LootrRegistry.getCopperChestBlock();
  }

  @Override
  public String getId() {
    return "copper_chest";
  }
}
