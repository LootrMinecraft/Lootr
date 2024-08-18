package noobanidus.mods.lootr.common.api;

import net.minecraft.server.level.ServerPlayer;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.data.entity.ILootrCart;

public class PlatformAPI {
  public static IPlatformAPI INSTANCE;

  public static void performCartOpen(ILootrCart cart, ServerPlayer player) {
    INSTANCE.performCartOpen(cart, player);
  }

  public static void performCartOpen(ILootrCart cart) {
    INSTANCE.performCartOpen(cart);
  }

  public static void performCartClose(ILootrCart cart, ServerPlayer player) {
    INSTANCE.performCartClose(cart, player);
  }

  public static void performCartClose(ILootrCart cart) {
    INSTANCE.performCartClose(cart);
  }

  public static void performBlockOpen(ILootrBlockEntity blockEntity, ServerPlayer player) {
    INSTANCE.performBlockOpen(blockEntity, player);
  }

  public static void performBlockOpen(ILootrBlockEntity blockEntity) {
    INSTANCE.performBlockOpen(blockEntity);
  }

  public static void performBlockClose(ILootrBlockEntity blockEntity, ServerPlayer player) {
    INSTANCE.performBlockClose(blockEntity, player);
  }

  public static void performBlockClose(ILootrBlockEntity blockEntity) {
    INSTANCE.performBlockClose(blockEntity);
  }
}
