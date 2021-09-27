package noobanidus.mods.lootr.commands;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import noobanidus.mods.lootr.api.ILootTile;
import noobanidus.mods.lootr.data.NewChestData;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;
import noobanidus.mods.lootr.init.ModBlocks;
import noobanidus.mods.lootr.tiles.SpecialLootInventoryTile;
import noobanidus.mods.lootr.util.ChestUtil;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class CommandLootr {
  private final CommandDispatcher<CommandSourceStack> dispatcher;

  public CommandLootr(CommandDispatcher<CommandSourceStack> dispatcher) {
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
      tables = new ArrayList<>(BuiltInLootTables.all());
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

  public static void createBlock(CommandSourceStack c, @Nullable Block block, @Nullable ResourceLocation table) {
    Level world = c.getLevel();
    BlockPos pos = new BlockPos(c.getPosition());
    if (table == null) {
      table = getTables().get(world.getRandom().nextInt(getTables().size()));
    }
    if (block == null) {
      LootrChestMinecartEntity cart = new LootrChestMinecartEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
      cart.setLootTable(table, world.getRandom().nextLong());
      world.addFreshEntity(cart);
      c.sendSuccess(new TranslatableComponent("lootr.commands.summon", ComponentUtils.wrapInSquareBrackets(new TranslatableComponent("lootr.commands.blockpos", pos.getX(), pos.getY(), pos.getZ()).setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.GREEN)).withBold(true))), table.toString()), false);
    } else {
      world.setBlock(pos, block.defaultBlockState(), 2);
      RandomizableContainerBlockEntity.setLootTable(world, world.getRandom(), pos, table);
      c.sendSuccess(new TranslatableComponent("lootr.commands.create", new TranslatableComponent(block.getDescriptionId()), ComponentUtils.wrapInSquareBrackets(new TranslatableComponent("lootr.commands.blockpos", pos.getX(), pos.getY(), pos.getZ()).setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.GREEN)).withBold(true))), table.toString()), false);
    }
  }

  private RequiredArgumentBuilder<CommandSourceStack, ResourceLocation> suggestTables() {
    return Commands.argument("table", ResourceLocationArgument.id())
        .suggests((c, build) -> SharedSuggestionProvider.suggest(getTableNames(), build));
  }

  private RequiredArgumentBuilder<CommandSourceStack, String> suggestProfiles() {
    return Commands.argument("profile", StringArgumentType.string()).suggests((c, build) -> SharedSuggestionProvider.suggest(getProfiles(), build));
  }

  public LiteralArgumentBuilder<CommandSourceStack> builder(LiteralArgumentBuilder<CommandSourceStack> builder) {
    builder.executes(c -> {
      c.getSource().sendSuccess(new TranslatableComponent("lootr.commands.usage"), false);
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
    builder.then(Commands.literal("clear").executes(c -> {
      c.getSource().sendSuccess(new TextComponent("Must provide player name."), true);
      return 1;
    }).then(suggestProfiles().executes(c -> {
      String playerName = StringArgumentType.getString(c, "profile");
      GameProfile profile = c.getSource().getServer().getProfileCache().get(playerName);
      if (profile == null) {
        c.getSource().sendFailure(new TextComponent("Invalid player name: " + playerName + ", profile not found in the cache."));
        return 0;
      }
      c.getSource().sendSuccess(new TextComponent(NewChestData.clearInventories(profile.getId()) ? "Cleared stored inventories for " + playerName : "No stored inventories for " + playerName + " to clear"), true);
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
      Level world = c.getSource().getLevel();
      BlockState state = world.getBlockState(pos);
      if (!state.is(Blocks.CHEST)) {
        pos = pos.below();
        state = world.getBlockState(pos);
      }
      if (!state.is(Blocks.CHEST)) {
        c.getSource().sendSuccess(new TextComponent("Please stand on the chest you wish to convert."), false);
      } else {
        NonNullList<ItemStack> reference = ((ChestBlockEntity) Objects.requireNonNull(world.getBlockEntity(pos))).items;
        NonNullList<ItemStack> custom = ChestUtil.copyItemList(reference);
        world.removeBlockEntity(pos);
        world.setBlockAndUpdate(pos, ModBlocks.INVENTORY.defaultBlockState().setValue(ChestBlock.FACING, state.getValue(ChestBlock.FACING)).setValue(ChestBlock.WATERLOGGED, state.getValue(ChestBlock.WATERLOGGED)));
        BlockEntity te = world.getBlockEntity(pos);
        if (!(te instanceof SpecialLootInventoryTile)) {
          c.getSource().sendSuccess(new TextComponent("Unable to convert chest, BlockState is not a Lootr Inventory block."), false);
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
      Level world = c.getSource().getLevel();
      BlockEntity te = world.getBlockEntity(pos);
      if (!(te instanceof ILootTile)) {
        pos = pos.below();
        te = world.getBlockEntity(pos);
      }
      if (!(te instanceof ILootTile)) {
        c.getSource().sendSuccess(new TextComponent("Please stand on a valid Lootr chest."), false);
      } else {
        c.getSource().sendSuccess(new TextComponent("The ID of this inventory is: " + ((ILootTile) te).getTileId().toString()), false);
      }
      return 1;
    }));
    builder.then(Commands.literal("openers").then(Commands.argument("location", Vec3Argument.vec3()).executes(c -> {
      BlockPos position = Vec3Argument.getCoordinates(c, "location").getBlockPos(c.getSource());
      Level world = c.getSource().getLevel();
      BlockEntity tile = world.getBlockEntity(position);
      if (tile instanceof ILootTile) {
        Set<UUID> openers = ((ILootTile) tile).getOpeners();
        c.getSource().sendSuccess(new TextComponent("Tile at location " + position + " has " + openers.size() + " openers. UUIDs as follows:"), true);
        for (UUID uuid : openers) {
          GameProfile profile = c.getSource().getServer().getProfileCache().get(uuid);
          c.getSource().sendSuccess(new TextComponent("UUID: " + uuid.toString() + ", user profile: " + (profile == null ? "null" : profile.getName())), true);
        }
      } else {
        c.getSource().sendSuccess(new TextComponent("No Lootr tile exists at location: " + position), false);
      }
      return 1;
    })));
    return builder;
  }
}

