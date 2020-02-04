package noobanidus.mods.lootr.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CommandChest {
  public static ResourceLocation table = new ResourceLocation("minecraft", "chests/igloo_chest");
  private final CommandDispatcher<CommandSource> dispatcher;

  public CommandChest(CommandDispatcher<CommandSource> dispatcher) {
    this.dispatcher = dispatcher;
  }

  public CommandChest register() {
    this.dispatcher.register(builder(Commands.literal("chest")));
    return this;
  }

  public LiteralArgumentBuilder<CommandSource> builder(LiteralArgumentBuilder<CommandSource> builder) {
    builder.executes(c -> {
      World world = c.getSource().getWorld();
      BlockPos pos = new BlockPos(c.getSource().getPos());
      world.setBlockState(pos, Blocks.CHEST.getDefaultState(), 2);
      if (world.getTileEntity(pos) instanceof LockableLootTileEntity) {
        LockableLootTileEntity.setLootTable(world, world.getRandom(), pos, table);
      }
      return 1;
    });
    return builder;
  }
}

