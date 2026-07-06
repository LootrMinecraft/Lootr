package noobanidus.mods.lootr.neoforge.gametest;

import com.mojang.authlib.GameProfile;
import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.AfterBatch;
import net.minecraft.gametest.framework.BeforeBatch;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.PotDecorations;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import noobanidus.mods.lootr.common.api.IPlatformAPI;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.PlatformAPI;
import noobanidus.mods.lootr.common.api.data.inventory.ILootrInventory;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;
import noobanidus.mods.lootr.common.block.entity.LootrDecoratedPotBlockEntity;

import java.util.UUID;

@PrefixGameTestTemplate(false)
public final class LootrDecoratedPotGameTests {
  private static final String BATCH = "lootrDecoratedPot";
  private static final BlockPos POT_POS = new BlockPos(1, 1, 1);
  private static final double ITEM_CHECK_RADIUS = 2.0;
  private static final ResourceKey<LootTable> EMPTY_POT_LOOT_TABLE = ResourceKey.create(Registries.LOOT_TABLE, LootrAPI.rl("gametest/empty_pot"));
  private static final ResourceKey<LootTable> NON_EMPTY_POT_LOOT_TABLE = ResourceKey.create(Registries.LOOT_TABLE, LootrAPI.rl("gametest/non_empty_pot"));
  private static final IPlatformAPI PLATFORM_API = new GameTestsPlatformAPI();
  private static IPlatformAPI originalPlatformApi;

  private LootrDecoratedPotGameTests() {
  }

  @BeforeBatch(batch = BATCH)
  public static void setUpPlatformApi(ServerLevel level) {
    originalPlatformApi = PlatformAPI.INSTANCE;
    PlatformAPI.INSTANCE = PLATFORM_API;
  }

  @AfterBatch(batch = BATCH)
  public static void tearDownPlatformApi(ServerLevel level) {
    PlatformAPI.INSTANCE = originalPlatformApi;
    originalPlatformApi = null;
  }

  @GameTest(templateNamespace = LootrAPI.MODID, batch = BATCH, template = "empty")
  public static void playersCanOpenEmptyPot(GameTestHelper helper) {
    helper.setBlock(POT_POS, LootrRegistry.getDecoratedPotBlock());
    LootrDecoratedPotBlockEntity pot = helper.getBlockEntity(POT_POS);
    pot.setLootTable(EMPTY_POT_LOOT_TABLE);

    ServerPlayer firstPlayer = createConnectedPlayer(helper, "lootr-player-1");
    ServerPlayer secondPlayer = createConnectedPlayer(helper, "lootr-player-2");

    if (!pot.dropContent(firstPlayer)) {
      helper.fail("Expected first player empty pot loot result to still count as dropped content");
    }
    assertPlayerOpened(helper, pot, firstPlayer, "first player empty pot loot result");
    assertPlayerInventoryEmpty(helper, pot, firstPlayer, "first player empty pot loot result");

    if (pot.dropContent(firstPlayer)) {
      helper.fail("Expected first player to only receive empty pot loot once");
    }

    if (!pot.dropContent(secondPlayer)) {
      helper.fail("Expected second player empty pot loot result to still count as dropped content");
    }
    assertPlayerOpened(helper, pot, secondPlayer, "second player empty pot loot result");
    assertPlayerInventoryEmpty(helper, pot, secondPlayer, "second player empty pot loot result");

    helper.succeed();
  }

  @GameTest(templateNamespace = LootrAPI.MODID, batch = BATCH, template = "empty")
  public static void playersCanOpenFilledPot(GameTestHelper helper) {
    helper.setBlock(POT_POS, LootrRegistry.getDecoratedPotBlock());
    LootrDecoratedPotBlockEntity pot = helper.getBlockEntity(POT_POS);
    pot.setLootTable(NON_EMPTY_POT_LOOT_TABLE);

    ServerPlayer firstPlayer = createConnectedPlayer(helper, "lootr-player-1");
    ServerPlayer secondPlayer = createConnectedPlayer(helper, "lootr-player-2");

    if (!pot.dropContent(firstPlayer)) {
      helper.fail("Expected first player non-empty pot loot result to drop content");
    }
    assertPlayerOpened(helper, pot, firstPlayer, "first player non-empty pot loot result");
    assertPlayerInventoryEmpty(helper, pot, firstPlayer, "first player non-empty pot loot result");
    helper.assertItemEntityCountIs(Items.DIAMOND, POT_POS.above(), ITEM_CHECK_RADIUS, 1);

    if (pot.dropContent(firstPlayer)) {
      helper.fail("Expected first player to only receive non-empty pot loot once");
    }
    helper.assertItemEntityCountIs(Items.DIAMOND, POT_POS.above(), ITEM_CHECK_RADIUS, 1);

    if (!pot.dropContent(secondPlayer)) {
      helper.fail("Expected second player non-empty pot loot result to drop content");
    }
    assertPlayerOpened(helper, pot, secondPlayer, "second player non-empty pot loot result");
    assertPlayerInventoryEmpty(helper, pot, secondPlayer, "second player non-empty pot loot result");
    helper.assertItemEntityCountIs(Items.DIAMOND, POT_POS.above(), ITEM_CHECK_RADIUS, 2);

    helper.succeed();
  }

  @GameTest(templateNamespace = LootrAPI.MODID, batch = BATCH, template = "empty")
  public static void playerCanBreakEmptyPot(GameTestHelper helper) {
    helper.setBlock(POT_POS, LootrRegistry.getDecoratedPotBlock());
    LootrDecoratedPotBlockEntity pot = helper.getBlockEntity(POT_POS);
    pot.setLootTable(EMPTY_POT_LOOT_TABLE);

    ServerPlayer firstPlayer = createConnectedPlayer(helper, "lootr-player-1");
    ServerPlayer secondPlayer = createConnectedPlayer(helper, "lootr-player-2");

    if (!pot.dropContent(firstPlayer)) {
      helper.fail("Expected first player empty pot loot result to still count as dropped content before breaking");
    }
    assertPlayerOpened(helper, pot, firstPlayer, "first player empty pot loot result before breaking");
    assertPlayerInventoryEmpty(helper, pot, firstPlayer, "first player empty pot loot result before breaking");

    setDecoratedPotDecorations(pot);
    breakPot(helper, secondPlayer);
    assertPlayerOpened(helper, pot, secondPlayer, "second player empty pot break result");
    assertPlayerInventoryEmpty(helper, pot, secondPlayer, "second player empty pot break result");
    helper.assertItemEntityNotPresent(Items.DIAMOND, POT_POS.above(), ITEM_CHECK_RADIUS);
    assertDecorationDrops(helper);
    helper.assertBlockNotPresent(LootrRegistry.getDecoratedPotBlock(), POT_POS);

    helper.succeed();
  }

  @GameTest(templateNamespace = LootrAPI.MODID, batch = BATCH, template = "empty")
  public static void playerCanBreakFilledPot(GameTestHelper helper) {
    helper.setBlock(POT_POS, LootrRegistry.getDecoratedPotBlock());
    LootrDecoratedPotBlockEntity pot = helper.getBlockEntity(POT_POS);
    pot.setLootTable(NON_EMPTY_POT_LOOT_TABLE);

    ServerPlayer firstPlayer = createConnectedPlayer(helper, "lootr-player-1");
    ServerPlayer secondPlayer = createConnectedPlayer(helper, "lootr-player-2");

    if (!pot.dropContent(firstPlayer)) {
      helper.fail("Expected first player non-empty pot loot result to drop content before breaking");
    }
    assertPlayerOpened(helper, pot, firstPlayer, "first player non-empty pot loot result before breaking");
    assertPlayerInventoryEmpty(helper, pot, firstPlayer, "first player non-empty pot loot result before breaking");
    helper.assertItemEntityCountIs(Items.DIAMOND, POT_POS.above(), ITEM_CHECK_RADIUS, 1);

    setDecoratedPotDecorations(pot);
    breakPot(helper, secondPlayer);
    assertPlayerOpened(helper, pot, secondPlayer, "second player non-empty pot break result");
    assertPlayerInventoryEmpty(helper, pot, secondPlayer, "second player non-empty pot break result");
    helper.assertItemEntityCountIs(Items.DIAMOND, POT_POS.above(), ITEM_CHECK_RADIUS, 2);
    assertDecorationDrops(helper);
    helper.assertBlockNotPresent(LootrRegistry.getDecoratedPotBlock(), POT_POS);

    helper.succeed();
  }

  private static void assertPlayerOpened(GameTestHelper helper, LootrDecoratedPotBlockEntity pot, ServerPlayer player, String description) {
    if (!pot.hasVisualOpened(player)) {
      helper.fail("Expected " + description + " to mark the player as a visual opener", POT_POS);
    }
    if (!pot.hasServerOpened(player)) {
      helper.fail("Expected " + description + " to mark the player as a server opener", POT_POS);
    }
  }

  private static void assertPlayerInventoryEmpty(GameTestHelper helper, LootrDecoratedPotBlockEntity pot, ServerPlayer player, String description) {
    ILootrInventory inventory = LootrAPI.getInventory(pot, player);
    if (inventory == null) {
      helper.fail("Expected " + description + " to create a player inventory", POT_POS);
    } else if (!inventory.isEmpty()) {
      helper.fail("Expected " + description + " to leave the player inventory empty", POT_POS);
    }
  }

  private static void setDecoratedPotDecorations(LootrDecoratedPotBlockEntity pot) {
    ItemStack potStack = LootrRegistry.getDecoratedPotItem().getDefaultInstance();
    potStack.set(DataComponents.POT_DECORATIONS, new PotDecorations(
        Items.ANGLER_POTTERY_SHERD,
        Items.ARCHER_POTTERY_SHERD,
        Items.ARMS_UP_POTTERY_SHERD,
        Items.BLADE_POTTERY_SHERD
    ));
    pot.setFromItem(potStack);
  }

  private static void assertDecorationDrops(GameTestHelper helper) {
    helper.assertItemEntityCountIs(Items.ANGLER_POTTERY_SHERD, POT_POS.above(), ITEM_CHECK_RADIUS, 1);
    helper.assertItemEntityCountIs(Items.ARCHER_POTTERY_SHERD, POT_POS.above(), ITEM_CHECK_RADIUS, 1);
    helper.assertItemEntityCountIs(Items.ARMS_UP_POTTERY_SHERD, POT_POS.above(), ITEM_CHECK_RADIUS, 1);
    helper.assertItemEntityCountIs(Items.BLADE_POTTERY_SHERD, POT_POS.above(), ITEM_CHECK_RADIUS, 1);
  }

  private static void breakPot(GameTestHelper helper, ServerPlayer player) {
    BlockPos absolutePotPos = helper.absolutePos(POT_POS);
    Vec3 center = Vec3.atCenterOf(absolutePotPos);
    player.moveTo(center.x, center.y, center.z, 0.0F, 0.0F);
    boolean originalInstabuild = player.getAbilities().instabuild;

    try {
      player.getAbilities().instabuild = false;
      player.gameMode.handleBlockBreakAction(absolutePotPos, ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, Direction.UP, helper.getLevel().getMaxBuildHeight(), 0);

      if (!helper.getLevel().destroyBlock(absolutePotPos, false, player)) {
        helper.fail("Expected the Lootr pot break to destroy the block", POT_POS);
      }
    } finally {
      player.getAbilities().instabuild = originalInstabuild;
    }
  }

  private static ServerPlayer createConnectedPlayer(GameTestHelper helper, String name) {
    GameProfile profile = new GameProfile(UUID.randomUUID(), name);
    CommonListenerCookie cookie = CommonListenerCookie.createInitial(profile, false);
    ServerPlayer player = new ServerPlayer(helper.getLevel().getServer(), helper.getLevel(), cookie.gameProfile(), cookie.clientInformation()) {
      @Override
      public boolean isSpectator() {
        return false;
      }

      @Override
      public boolean isCreative() {
        return true;
      }
    };
    Connection connection = new Connection(PacketFlow.SERVERBOUND);
    new EmbeddedChannel(connection);
    new ServerGamePacketListenerImpl(helper.getLevel().getServer(), connection, player, cookie);
    player.getAbilities().mayBuild = true;
    return player;
  }

}
