package noobanidus.mods.lootr.neoforge.mixin.self;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.extensions.IBlockEntityExtension;
import net.neoforged.neoforge.model.data.ModelData;
import noobanidus.mods.lootr.common.api.data.ILootrInfoProvider;
import noobanidus.mods.lootr.common.block.entity.LootrBrushableBlockEntity;
import noobanidus.mods.lootr.common.client.ClientHooks;
import noobanidus.mods.lootr.neoforge.block.ModelDataConstants;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LootrBrushableBlockEntity.class)
public abstract class MixinLootrBrushableBlockEntity implements IBlockEntityExtension {
  @Override
  public ModelData getModelData() {
    ILootrInfoProvider provider = (ILootrInfoProvider) this;
    Player player = ClientHooks.getPlayer();
    if (player == null || !provider.hasClientOpened(player.getUUID())) {
      return ModelDataConstants.CLOSED_MODEL_DATA;
    } else {
      return ModelDataConstants.OPENED_MODEL_DATA;
    }
  }
}
