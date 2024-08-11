package noobanidus.mods.lootr.fabric.mixins.client;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.fabric.ClientHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class MixinClientPacketListener {
  @Inject(method="method_38542", at=@At("RETURN"))
  private void LootrInjectClientBlockEntityUpdateTag(ClientboundBlockEntityDataPacket clientboundBlockEntityDataPacket, BlockEntity blockEntity, CallbackInfo ci) {
    if (blockEntity instanceof ILootrBlockEntity) {
      ClientHooks.clearCache(blockEntity.getBlockPos());
    }
  }
}
