package noobanidus.mods.lootr.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.data.LootTableProvider;
import net.minecraft.loot.LootTables;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import noobanidus.mods.lootr.init.ModBlocks;
import noobanidus.mods.lootr.tiles.ILootTile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CommandBarrel {
  private final CommandDispatcher<CommandSource> dispatcher;

  public CommandBarrel(CommandDispatcher<CommandSource> dispatcher) {
    this.dispatcher = dispatcher;
  }

  public CommandBarrel register() {
    this.dispatcher.register(builder(Commands.literal("barrel").requires(p -> p.hasPermissionLevel(2))));
    return this;
  }

  public LiteralArgumentBuilder<CommandSource> builder(LiteralArgumentBuilder<CommandSource> builder) {
    builder.executes(c -> {
      World world = c.getSource().getWorld();
      BlockPos pos = new BlockPos(c.getSource().getPos());
      world.setBlockState(pos, Blocks.BARREL.getDefaultState(), 2);
      List<ResourceLocation> tables = new ArrayList<>(LootTables.getReadOnlyLootTables());
      ResourceLocation table = tables.get(world.getRandom().nextInt(tables.size()));
      LockableLootTileEntity.setLootTable(world, world.getRandom(), pos, table);
      c.getSource().sendFeedback(new StringTextComponent("Created a Loot Barrel at " + pos.getX() + "," + pos.getY() + "," + pos.getZ() + " using the loot table: " + table.toString()), false);
      return 1;
    });
    builder.then(Commands.argument("table", ResourceLocationArgument.resourceLocation()).suggests((c, build) -> ISuggestionProvider.suggest(LootTables.getReadOnlyLootTables().stream().map(ResourceLocation::toString).collect(Collectors.toList()), build)).executes(c -> {
      ResourceLocation table = ResourceLocationArgument.getResourceLocation(c, "table");
      World world = c.getSource().getWorld();
      BlockPos pos = new BlockPos(c.getSource().getPos());
      world.setBlockState(pos, Blocks.BARREL.getDefaultState(), 2);
      LockableLootTileEntity.setLootTable(world, world.getRandom(), pos, table);
      c.getSource().sendFeedback(new StringTextComponent("Created a Loot Barrel at " + pos.getX() + "," + pos.getY() + "," + pos.getZ() + " using the loot table: " + table.toString()), false);
      return 1;
    }));
    return builder;
  }
}

