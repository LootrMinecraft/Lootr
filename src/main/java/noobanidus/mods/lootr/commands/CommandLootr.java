package noobanidus.mods.lootr.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.loot.LootTables;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;
import noobanidus.mods.lootr.init.ModBlocks;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
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

  private static List<ResourceLocation> getTables() {
    if (tables == null) {
      tables = new ArrayList<>(LootTables.getReadOnlyLootTables());
      tableNames = tables.stream().map(ResourceLocation::toString).collect(Collectors.toList());
    }
    return tables;
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
    builder.then(Commands.literal("cart").executes(c -> {
      createBlock(c.getSource(), null, null);
      return 1;
    }).then(suggestTables().executes(c -> {
      createBlock(c.getSource(), null, ResourceLocationArgument.getResourceLocation(c, "table"));
      return 1;
    })));
    builder.then(Commands.literal("custom").executes(c -> {
      BlockPos pos = new BlockPos(c.getSource().getPos()).down();
      World world = c.getSource().getWorld();
      BlockState state = world.getBlockState(pos);
      if (!state.isIn(Blocks.CHEST)) {
        c.getSource().sendFeedback(new StringTextComponent("Please stand on the chest you wish to convert."), false);
      }
      return 1;
    }));
    return builder;
  }
}

