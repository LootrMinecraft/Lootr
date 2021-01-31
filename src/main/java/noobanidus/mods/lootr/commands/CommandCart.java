package noobanidus.mods.lootr.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.loot.LootTables;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommandCart {
  private final CommandDispatcher<CommandSource> dispatcher;

  public CommandCart(CommandDispatcher<CommandSource> dispatcher) {
    this.dispatcher = dispatcher;
  }

  public CommandCart register() {
    this.dispatcher.register(builder(Commands.literal("cart").requires(p -> p.hasPermissionLevel(2))));
    return this;
  }

  public LiteralArgumentBuilder<CommandSource> builder(LiteralArgumentBuilder<CommandSource> builder) {
    builder.executes(c -> {
      World world = c.getSource().getWorld();
      BlockPos pos = new BlockPos(c.getSource().getPos());
      List<ResourceLocation> tables = new ArrayList<>(LootTables.getReadOnlyLootTables());
      ResourceLocation table = tables.get(world.getRandom().nextInt(tables.size()));
      LootrChestMinecartEntity entity = new LootrChestMinecartEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
      entity.setLootTable(table, world.getRandom().nextLong());
      world.addEntity(entity);
      c.getSource().sendFeedback(new StringTextComponent("Created a Loot Cart at " + pos.getX() + "," + pos.getY() + "," + pos.getZ() + " using the loot table: " + table.toString()), false);
      return 1;
    });
    builder.then(Commands.argument("table", ResourceLocationArgument.resourceLocation()).suggests((c, build) -> ISuggestionProvider.suggest(LootTables.getReadOnlyLootTables().stream().map(ResourceLocation::toString).collect(Collectors.toList()), build)).executes(c -> {
      ResourceLocation table = ResourceLocationArgument.getResourceLocation(c, "table");
      World world = c.getSource().getWorld();
      BlockPos pos = new BlockPos(c.getSource().getPos());
      LootrChestMinecartEntity entity = new LootrChestMinecartEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
      entity.setLootTable(table, world.getRandom().nextLong());
      world.addEntity(entity);
      c.getSource().sendFeedback(new StringTextComponent("Created a Loot Cart at " + pos.getX() + "," + pos.getY() + "," + pos.getZ() + " using the loot table: " + table.toString()), false);
      return 1;
    }));
    return builder;
  }
}


