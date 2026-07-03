package noobanidus.mods.lootr.common.client.state;

import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import noobanidus.mods.lootr.common.api.LootrChestType;

public class LootrChestBlockRenderState extends ChestRenderState {
  public boolean visuallyOpen;
  public boolean classic;
  public boolean vanilla;
  public LootrChestType chestType;
}
