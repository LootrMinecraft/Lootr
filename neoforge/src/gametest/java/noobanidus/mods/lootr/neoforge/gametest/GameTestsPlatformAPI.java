package noobanidus.mods.lootr.neoforge.gametest;

import net.minecraft.server.level.ServerPlayer;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.data.entity.ILootrEntity;
import noobanidus.mods.lootr.neoforge.impl.PlatformAPIImpl;

final class GameTestsPlatformAPI extends PlatformAPIImpl {
  @Override
  public void performEntityOpen(ILootrEntity entity, ServerPlayer player) {
  }

  @Override
  public void performEntityOpen(ILootrEntity entity) {
  }

  @Override
  public void performEntityClose(ILootrEntity entity, ServerPlayer player) {
  }

  @Override
  public void performEntityClose(ILootrEntity entity) {
  }

  @Override
  public void performBlockOpen(ILootrBlockEntity blockEntity, ServerPlayer player) {
  }

  @Override
  public void performBlockOpen(ILootrBlockEntity blockEntity) {
  }

  @Override
  public void performBlockClose(ILootrBlockEntity blockEntity, ServerPlayer player) {
  }

  @Override
  public void performBlockClose(ILootrBlockEntity blockEntity) {
  }

  @Override
  public void refreshPlayerSection(ServerPlayer player) {
  }

  @Override
  public void performPotBreak(ILootrBlockEntity blockEntity, ServerPlayer player) {
  }
}
