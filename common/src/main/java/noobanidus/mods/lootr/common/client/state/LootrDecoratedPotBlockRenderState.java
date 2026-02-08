package noobanidus.mods.lootr.common.client.state;

import net.minecraft.client.renderer.blockentity.state.DecoratedPotRenderState;
import noobanidus.mods.lootr.common.api.integration.PotDecorationsAdapter;

public class LootrDecoratedPotBlockRenderState extends DecoratedPotRenderState {
  public boolean visuallyOpen;
  public PotDecorationsAdapter potDecorations;
}
