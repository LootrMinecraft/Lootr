package noobanidus.mods.lootr.commands;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTables;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import noobanidus.mods.lootr.data.NewChestData;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;
import noobanidus.mods.lootr.init.ModBlocks;
import noobanidus.mods.lootr.tiles.ILootTile;
import noobanidus.mods.lootr.tiles.SpecialLootInventoryTile;
import noobanidus.mods.lootr.util.ChestUtil;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class CommandLootr {
  private final CommandDispatcher<CommandSource> dispatcher;

  public CommandLootr(CommandDispatcher<CommandSource> dispatcher) {
    this.dispatcher = dispatcher;
  }

  public CommandLootr register() {
    this.dispatcher.register(builder(Commands.literal("lootr").requires(p -> p.hasPermissionLevel(2))));
    return this;
  }

  private static List<ResourceLocation> tables = null;
  private static List<String> tableNames = null;
  private static Map<String, UUID> profileMap = new HashMap<>();

  private static List<ResourceLocation> getTables() {
    if (tables == null) {
      tables = new ArrayList<>(LootTables.getReadOnlyLootTables());
      tableNames = tables.stream().map(ResourceLocation::toString).collect(Collectors.toList());
    }
    return tables;
  }

  private static List<String> getProfiles() {
    return Lists.newArrayList(ServerLifecycleHooks.getCurrentServer().getPlayerProfileCache().usernameToProfileEntryMap.keySet());
  }

  private static List<String> getTableNames() {
    getTables();
    return tableNames;
  }

  public static void createBlock(CommandSource c, @Nullable Block block, @Nullable ResourceLocation table) {
    World world = c.getWorld();
    BlockPos pos = new BlockPos(c.getPos());
    if (table == null) {
      table = getTables().get(world.getRandom().nextInt(getTables().size()));
    }
    if (block == null) {
      LootrChestMinecartEntity cart = new LootrChestMinecartEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
      cart.setLootTable(table, world.getRandom().nextLong());
      world.addEntity(cart);
      c.sendFeedback(new TranslationTextComponent("lootr.commands.summon", TextComponentUtils.wrapWithSquareBrackets(new TranslationTextComponent("lootr.commands.blockpos", pos.getX(), pos.getY(), pos.getZ()).setStyle(Style.EMPTY.setColor(Color.fromTextFormatting(TextFormatting.GREEN)).setBold(true))), table.toString()), false);
    } else {
      world.setBlockState(pos, block.getDefaultState(), 2);
      LockableLootTileEntity.setLootTable(world, world.getRandom(), pos, table);
      c.sendFeedback(new TranslationTextComponent("lootr.commands.create", new TranslationTextComponent(block.getTranslationKey()), TextComponentUtils.wrapWithSquareBrackets(new TranslationTextComponent("lootr.commands.blockpos", pos.getX(), pos.getY(), pos.getZ()).setStyle(Style.EMPTY.setColor(Color.fromTextFormatting(TextFormatting.GREEN)).setBold(true))), table.toString()), false);
    }
  }

  private RequiredArgumentBuilder<CommandSource, ResourceLocation> suggestTables() {
    return Commands.argument("table", ResourceLocationArgument.resourceLocation())
        .suggests((c, build) -> ISuggestionProvider.suggest(getTableNames(), build));
  }

  private RequiredArgumentBuilder<CommandSource, String> suggestProfiles() {
    return Commands.argument("profile", StringArgumentType.string()).suggests((c, build) -> ISuggestionProvider.suggest(getProfiles(), build));
  }

  public LiteralArgumentBuilder<CommandSource> builder(LiteralArgumentBuilder<CommandSource> builder) {
    builder.executes(c -> {
      c.getSource().sendFeedback(new TranslationTextComponent("lootr.commands.usage"), false);
      return 1;
    });
    builder.then(Commands.literal("barrel").executes(c -> {
      createBlock(c.getSource(), ModBlocks.BARREL, null);
      return 1;
    }).then(suggestTables().executes(c -> {
      createBlock(c.getSource(), ModBlocks.BARREL, ResourceLocationArgument.getResourceLocation(c, "table"));
      return 1;
    })));
    builder.then(Commands.literal("chest").executes(c -> {
      createBlock(c.getSource(), ModBlocks.CHEST, null);
      return 1;
    }).then(suggestTables().executes(c -> {
      createBlock(c.getSource(), ModBlocks.CHEST, ResourceLocationArgument.getResourceLocation(c, "table"));
      return 1;
    })));
    builder.then(Commands.literal("clear").executes(c -> {
      c.getSource().sendFeedback(new StringTextComponent("Must provide player name."), true);
      return 1;
    }).then(suggestProfiles().executes(c -> {
      String playerName = StringArgumentType.getString(c, "profile");
      GameProfile profile = c.getSource().getServer().getPlayerProfileCache().getGameProfileForUsername(playerName);
      if (profile == null) {
        c.getSource().sendErrorMessage(new StringTextComponent("Invalid player name: " + playerName + ", profile not found in the cache."));
        return 0;
      }
      c.getSource().sendFeedback(new StringTextComponent(NewChestData.clearInventories(profile.getId()) ? "Cleared stored inventories for " + playerName : "No stored inventories for " + playerName + " to clear"), true);
      return 1;
    })));
    builder.then(Commands.literal("cart").executes(c -> {
      createBlock(c.getSource(), null, null);
      return 1;
    }).then(suggestTables().executes(c -> {
      createBlock(c.getSource(), null, ResourceLocationArgument.getResourceLocation(c, "table"));
      return 1;
    })));
    builder.then(Commands.literal("custom").executes(c -> {
      BlockPos pos = new BlockPos(c.getSource().getPos());
      World world = c.getSource().getWorld();
      BlockState state = world.getBlockState(pos);
      if (!state.isIn(Blocks.CHEST)) {
        pos = pos.down();
        state = world.getBlockState(pos);
      }
      if (!state.isIn(Blocks.CHEST)) {
        c.getSource().sendFeedback(new StringTextComponent("Please stand on the chest you wish to convert."), false);
      } else {
        NonNullList<ItemStack> reference = ((ChestTileEntity) Objects.requireNonNull(world.getTileEntity(pos))).chestContents;
        NonNullList<ItemStack> custom = ChestUtil.copyItemList(reference);
        world.removeTileEntity(pos);
        world.setBlockState(pos, ModBlocks.INVENTORY.getDefaultState().with(ChestBlock.FACING, state.get(ChestBlock.FACING)).with(ChestBlock.WATERLOGGED, state.get(ChestBlock.WATERLOGGED)));
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof SpecialLootInventoryTile)) {
          c.getSource().sendFeedback(new StringTextComponent("Unable to convert chest, BlockState is not a Lootr Inventory block."), false);
        } else {
          SpecialLootInventoryTile inventory = (SpecialLootInventoryTile) te;
          inventory.setCustomInventory(custom);
          inventory.markDirty();
        }
      }
      return 1;
    }));
    builder.then(Commands.literal("id").executes(c -> {
      BlockPos pos = new BlockPos(c.getSource().getPos());
      World world = c.getSource().getWorld();
      TileEntity te = world.getTileEntity(pos);
      if (!(te instanceof ILootTile)) {
        pos = pos.down();
        te = world.getTileEntity(pos);
      }
      if (!(te instanceof ILootTile)) {
        c.getSource().sendFeedback(new StringTextComponent("Please stand on a valid Lootr chest."), false);
      } else {
        c.getSource().sendFeedback(new StringTextComponent("The ID of this inventory is: " + ((ILootTile) te).getTileId().toString()), false);
      }
      return 1;
    }));
    return builder;
  }
}

