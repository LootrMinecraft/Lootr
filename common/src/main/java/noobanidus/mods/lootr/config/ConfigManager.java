package noobanidus.mods.lootr.config;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.api.blockentity.ILootBlockEntity;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;

public class ConfigManager {
  @ExpectPlatform
  public static boolean reportUnresolvedTables() {
    throw new AssertionError();
  }


  @ExpectPlatform
  public static boolean randomizeSeed() {
    throw new AssertionError();
  }

  @ExpectPlatform
  public static boolean isBlacklisted(ResourceLocation lootTable) {
    throw new AssertionError();
  }

  @ExpectPlatform
  public static BlockState replacement(BlockState stateAt) {
    throw new AssertionError();
  }

  @ExpectPlatform
  public static boolean hasLootStructureBlacklist() {
    throw new AssertionError();
  }

  @ExpectPlatform
  public static boolean isLootStructureBlacklisted(ResourceLocation key) {
    throw new AssertionError();
  }

  @ExpectPlatform
  public static boolean isDimensionBlocked(ResourceKey<Level> dimension) {
    throw new AssertionError();
  }

  @ExpectPlatform
  public static boolean isDecaying(ServerLevel level, ILootBlockEntity te) {
    throw new AssertionError();
  }

  @ExpectPlatform
  public static int getDecayValue() {
    throw new AssertionError();
  }

  @ExpectPlatform
  public static boolean isRefreshing(ServerLevel level, ILootBlockEntity te) {
    throw new AssertionError();
  }

  @ExpectPlatform
  public static int getRefreshValue() {
    throw new AssertionError();
  }

  @ExpectPlatform
  public static boolean isRefreshing(ServerLevel level, LootrChestMinecartEntity cart) {
    throw new AssertionError();
  }

  @ExpectPlatform
  public static boolean isDecaying(ServerLevel level, LootrChestMinecartEntity cart) {
    throw new AssertionError();
  }
}
