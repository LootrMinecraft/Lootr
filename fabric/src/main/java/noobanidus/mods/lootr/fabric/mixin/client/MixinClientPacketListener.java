package noobanidus.mods.lootr.fabric.mixin.client;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.client.ClientHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// TODO: Needs to target the lambda
// Does this even do anything at this point?
@Mixin(ClientPacketListener.class)
public class MixinClientPacketListener {
  @Inject(method="handleBlockEntityData", at=@At("RETURN"))
  private void LootrInjectClientBlockEntityUpdateTag(ClientboundBlockEntityDataPacket clientboundBlockEntityDataPacket, BlockEntity blockEntity, CallbackInfo ci) {
    if (LootrAPI.resolveBlockEntity(blockEntity) instanceof ILootrBlockEntity) {
      ClientHooks.clearCache(blockEntity.getBlockPos());
    }
  }
}
