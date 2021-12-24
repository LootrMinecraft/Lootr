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
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTables;
import net.minecraft.state.EnumProperty;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import noobanidus.mods.lootr.api.ILootTile;
import noobanidus.mods.lootr.blocks.LootrBarrelBlock;
import noobanidus.mods.lootr.blocks.LootrChestBlock;
import noobanidus.mods.lootr.blocks.LootrShulkerBlock;
import noobanidus.mods.lootr.data.DataStorage;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;
import noobanidus.mods.lootr.init.ModBlocks;
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
    this.dispatcher.register(builder(Commands.literal("lootr").requires(p -> p.hasPermission(2))));
    return this;
  }

  private static List<ResourceLocation> tables = null;
  private static List<String> tableNames = null;
  private static Map<String, UUID> profileMap = new HashMap<>();

  private static List<ResourceLocation> getTables() {
    if (tables == null) {
      tables = new ArrayList<>(LootTables.all());
      tableNames = tables.stream().map(ResourceLocation::toString).collect(Collectors.toList());
    }
    return tables;
  }

  private static List<String> getProfiles() {
    return Lists.newArrayList(ServerLifecycleHooks.getCurrentServer().getProfileCache().profilesByName.keySet());
  }

  private static List<String> getTableNames() {
    getTables();
    return tableNames;
  }

  public static void createBlock(CommandSource c, @Nullable Block block, @Nullable ResourceLocation table) {
    World world = c.getLevel();
    BlockPos pos = new BlockPos(c.getPosition());
    if (table == null) {
      table = getTables().get(world.getRandom().nextInt(getTables().size()));
    }
    if (block == null) {
      LootrChestMinecartEntity cart = new LootrChestMinecartEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
      Entity e = c.getEntity();
      if (e != null) {
        cart.yRot = e.yRot;
      }
      cart.setLootTable(table, world.getRandom().nextLong());
      world.addFreshEntity(cart);
      c.sendSuccess(new TranslationTextComponent("lootr.commands.summon", TextComponentUtils.wrapInSquareBrackets(new TranslationTextComponent("lootr.commands.blockpos", pos.getX(), pos.getY(), pos.getZ()).setStyle(Style.EMPTY.withColor(Color.fromLegacyFormat(TextFormatting.GREEN)).withBold(true))), table.toString()), false);
    } else {
      BlockState placementState = block.defaultBlockState();
      Entity e = c.getEntity();
      if (e != null) {
        EnumProperty<Direction> prop = null;
        Direction dir = Direction.orderedByNearest(e)[0].getOpposite();
        if (placementState.hasProperty(LootrBarrelBlock.FACING)) {
          prop = LootrBarrelBlock.FACING;
        } else if (placementState.hasProperty(LootrChestBlock.FACING)) {
          prop = LootrChestBlock.FACING;
          dir = e.getDirection().getOpposite();
        } else if (placementState.hasProperty(LootrShulkerBlock.FACING)) {
          prop = LootrShulkerBlock.FACING;
        }
        if (prop != null) {
          placementState = placementState.setValue(prop, dir);
        }
      }
      world.setBlock(pos, placementState, 2);
      LockableLootTileEntity.setLootTable(world, world.getRandom(), pos, table);
      c.sendSuccess(new TranslationTextComponent("lootr.commands.create", new TranslationTextComponent(block.getDescriptionId()), TextComponentUtils.wrapInSquareBrackets(new TranslationTextComponent("lootr.commands.blockpos", pos.getX(), pos.getY(), pos.getZ()).setStyle(Style.EMPTY.withColor(Color.fromLegacyFormat(TextFormatting.GREEN)).withBold(true))), table.toString()), false);
    }
  }

  private RequiredArgumentBuilder<CommandSource, ResourceLocation> suggestTables() {
    return Commands.argument("table", ResourceLocationArgument.id())
        .suggests((c, build) -> ISuggestionProvider.suggest(getTableNames(), build));
  }

  private RequiredArgumentBuilder<CommandSource, String> suggestProfiles() {
    return Commands.argument("profile", StringArgumentType.string()).suggests((c, build) -> ISuggestionProvider.suggest(getProfiles(), build));
  }

  public LiteralArgumentBuilder<CommandSource> builder(LiteralArgumentBuilder<CommandSource> builder) {
    builder.executes(c -> {
      c.getSource().sendSuccess(new TranslationTextComponent("lootr.commands.usage"), false);
      return 1;
    });
    builder.then(Commands.literal("barrel").executes(c -> {
      createBlock(c.getSource(), ModBlocks.BARREL, null);
      return 1;
    }).then(suggestTables().executes(c -> {
      createBlock(c.getSource(), ModBlocks.BARREL, ResourceLocationArgument.getId(c, "table"));
      return 1;
    })));
    builder.then(Commands.literal("chest").executes(c -> {
      createBlock(c.getSource(), ModBlocks.CHEST, null);
      return 1;
    }).then(suggestTables().executes(c -> {
      createBlock(c.getSource(), ModBlocks.CHEST, ResourceLocationArgument.getId(c, "table"));
      return 1;
    })));
    builder.then(Commands.literal("shulker").executes(c -> {
      createBlock(c.getSource(), ModBlocks.SHULKER, null);
      return 1;
    }).then(suggestTables().executes(c -> {
      createBlock(c.getSource(), ModBlocks.SHULKER, ResourceLocationArgument.getId(c, "table"));
      return 1;
    })));
    builder.then(Commands.literal("clear").executes(c -> {
      c.getSource().sendSuccess(new StringTextComponent("Must provide player name."), true);
      return 1;
    }).then(suggestProfiles().executes(c -> {
      String playerName = StringArgumentType.getString(c, "profile");
      GameProfile profile = c.getSource().getServer().getProfileCache().get(playerName);
      if (profile == null) {
        c.getSource().sendFailure(new StringTextComponent("Invalid player name: " + playerName + ", profile not found in the cache."));
        return 0;
      }
      c.getSource().sendSuccess(new StringTextComponent(DataStorage.clearInventories(profile.getId()) ? "Cleared stored inventories for " + playerName : "No stored inventories for " + playerName + " to clear"), true);
      return 1;
    })));
    builder.then(Commands.literal("cart").executes(c -> {
      createBlock(c.getSource(), null, null);
      return 1;
    }).then(suggestTables().executes(c -> {
      createBlock(c.getSource(), null, ResourceLocationArgument.getId(c, "table"));
      return 1;
    })));
    builder.then(Commands.literal("custom").executes(c -> {
      BlockPos pos = new BlockPos(c.getSource().getPosition());
      World world = c.getSource().getLevel();
      BlockState state = world.getBlockState(pos);
      if (!state.is(Blocks.CHEST)) {
        pos = pos.below();
        state = world.getBlockState(pos);
      }
      if (!state.is(Blocks.CHEST)) {
        c.getSource().sendSuccess(new StringTextComponent("Please stand on the chest you wish to convert."), false);
      } else {
        NonNullList<ItemStack> reference = ((ChestTileEntity) Objects.requireNonNull(world.getBlockEntity(pos))).items;
        NonNullList<ItemStack> custom = ChestUtil.copyItemList(reference);
        world.removeBlockEntity(pos);
        world.setBlockAndUpdate(pos, ModBlocks.INVENTORY.defaultBlockState().setValue(ChestBlock.FACING, state.getValue(ChestBlock.FACING)).setValue(ChestBlock.WATERLOGGED, state.getValue(ChestBlock.WATERLOGGED)));
        TileEntity te = world.getBlockEntity(pos);
        if (!(te instanceof SpecialLootInventoryTile)) {
          c.getSource().sendSuccess(new StringTextComponent("Unable to convert chest, BlockState is not a Lootr Inventory block."), false);
        } else {
          SpecialLootInventoryTile inventory = (SpecialLootInventoryTile) te;
          inventory.setCustomInventory(custom);
          inventory.setChanged();
        }
      }
      return 1;
    }));
    builder.then(Commands.literal("id").executes(c -> {
      BlockPos pos = new BlockPos(c.getSource().getPosition());
      World world = c.getSource().getLevel();
      TileEntity te = world.getBlockEntity(pos);
      if (!(te instanceof ILootTile)) {
        pos = pos.below();
        te = world.getBlockEntity(pos);
      }
      if (!(te instanceof ILootTile)) {
        c.getSource().sendSuccess(new StringTextComponent("Please stand on a valid Lootr chest."), false);
      } else {
        c.getSource().sendSuccess(new StringTextComponent("The ID of this inventory is: " + ((ILootTile) te).getTileId().toString()), false);
      }
      return 1;
    }));
    builder.then(Commands.literal("openers").then(Commands.argument("location", Vec3Argument.vec3()).executes(c -> {
      BlockPos position = Vec3Argument.getCoordinates(c, "location").getBlockPos(c.getSource());
      World world = c.getSource().getLevel();
      TileEntity tile = world.getBlockEntity(position);
      if (tile instanceof ILootTile) {
        Set<UUID> openers = ((ILootTile) tile).getOpeners();
        c.getSource().sendSuccess(new StringTextComponent("Tile at location " + position + " has " + openers.size() + " openers. UUIDs as follows:"), true);
        for (UUID uuid : openers) {
          GameProfile profile = c.getSource().getServer().getProfileCache().get(uuid);
          c.getSource().sendSuccess(new StringTextComponent("UUID: " + uuid.toString() + ", user profile: " + (profile == null ? "null" : profile.getName())), true);
        }
      } else {
        c.getSource().sendSuccess(new StringTextComponent("No Lootr tile exists at location: " + position), false);
      }
      return 1;
    })));
    return builder;
  }
}

