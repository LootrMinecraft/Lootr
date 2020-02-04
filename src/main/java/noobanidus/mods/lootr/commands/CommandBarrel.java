package noobanidus.mods.lootr.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class CommandBarrel {
  public static ResourceLocation table = new ResourceLocation("minecraft", "chests/igloo_chest");
  private final CommandDispatcher<CommandSource> dispatcher;

  public CommandBarrel(CommandDispatcher<CommandSource> dispatcher) {
    this.dispatcher = dispatcher;
  }

  public CommandBarrel register() {
    this.dispatcher.register(builder(Commands.literal("barrel")));
    return this;
  }

  public LiteralArgumentBuilder<CommandSource> builder(LiteralArgumentBuilder<CommandSource> builder) {
    builder.executes(c -> {
      World world = c.getSource().getWorld();
      BlockPos pos = new BlockPos(c.getSource().getPos());
      world.setBlockState(pos, Blocks.BARREL.getDefaultState(), 2);
      if (world.getTileEntity(pos) instanceof LockableLootTileEntity) {
        LockableLootTileEntity.setLootTable(world, world.getRandom(), pos, table);
      }
      return 1;
    });
    return builder;
  }
}

