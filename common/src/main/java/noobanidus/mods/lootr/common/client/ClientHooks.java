package noobanidus.mods.lootr.common.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class ClientHooks {
  @Nullable
  public static Player getPlayer() {
    Minecraft mc = Minecraft.getInstance();
    //noinspection ConstantValue
    if (mc == null) {
      return null;
    }
    return mc.player;
  }

  public static void clearCache(BlockPos position) {
    SectionPos pos = SectionPos.of(position);
    Minecraft.getInstance().levelRenderer.setSectionDirty(pos.x(), pos.y(), pos.z());
  }

  public static void refreshSection () {
    Player player = getPlayer();
    if (player != null) {
      clearCache(player.blockPosition());
    }
  }
}
