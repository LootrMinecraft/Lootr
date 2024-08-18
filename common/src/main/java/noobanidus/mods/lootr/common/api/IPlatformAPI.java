package noobanidus.mods.lootr.common.api;

import net.minecraft.server.level.ServerPlayer;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.data.entity.ILootrCart;

public interface IPlatformAPI {
  void performCartOpen(ILootrCart cart, ServerPlayer player);

  void performCartOpen(ILootrCart cart);

  void performCartClose(ILootrCart cart, ServerPlayer player);

  void performCartClose(ILootrCart cart);

  void performBlockOpen(ILootrBlockEntity blockEntity, ServerPlayer player);

  void performBlockOpen(ILootrBlockEntity blockEntity);

  void performBlockClose(ILootrBlockEntity blockEntity, ServerPlayer player);

  void performBlockClose(ILootrBlockEntity blockEntity);
}
