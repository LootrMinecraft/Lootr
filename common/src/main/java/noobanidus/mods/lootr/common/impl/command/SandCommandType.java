package noobanidus.mods.lootr.common.impl.command;

import com.google.auto.service.AutoService;
import net.minecraft.world.level.block.Block;
import noobanidus.mods.lootr.common.api.interfaces.command.ILootrCommandExtension;
import noobanidus.mods.lootr.common.api.LootrRegistry;

@AutoService(ILootrCommandExtension.class)
public class SandCommandType implements ILootrCommandExtension {
  @Override
  public Block getBlock() {
    return LootrRegistry.getSuspiciousSandBlock();
  }

  @Override
  public String getId() {
    return "sand";
  }
}
