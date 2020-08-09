package noobanidus.mods.lootr.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.dimension.DimensionType;
import noobanidus.mods.lootr.util.TickManager;

import java.util.Map;

public class CommandLootr {
  private final CommandDispatcher<CommandSource> dispatcher;

  public CommandLootr(CommandDispatcher<CommandSource> dispatcher) {
    this.dispatcher = dispatcher;
  }

  public CommandLootr register() {
    this.dispatcher.register(builder(Commands.literal("lootr")).requires(c -> c.hasPermissionLevel(4)));
    return this;
  }

  private String format(double value) {
    return String.format("%.2f", value);
  }

  public LiteralArgumentBuilder<CommandSource> builder(LiteralArgumentBuilder<CommandSource> builder) {
    builder.executes(c -> {
      synchronized (TickManager.lootMap) {
        int i = 0;
        for (Map.Entry<GlobalPos, ResourceLocation> entry : TickManager.lootMap.entrySet()) {
          BlockPos pos = entry.getKey().getPos();
          DimensionType dim = entry.getKey().getDimension();
          ResourceLocation rl = entry.getValue();
          if (c.getSource().asPlayer().world.getDimension().getType() == dim || dim == null) {
            c.getSource().sendFeedback(new StringTextComponent("#" + i + " Loot Chest" + (dim == null ? " (no dimension)" : "") + " at ").appendSibling(TextComponentUtils.wrapInSquareBrackets(new StringTextComponent(format(pos.getX()) + ", " + format(pos.getZ()))).setStyle(new Style().setColor(TextFormatting.GREEN).setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + pos.getX() + " " + pos.getY() + " " + pos.getZ())).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("Teleport to location"))))).appendSibling(new StringTextComponent(" using loot table: " + rl)), true);
          } else {
            c.getSource().sendFeedback(new StringTextComponent("#" + i + " Loot Chest in " + dim.toString() + "at " + format(pos.getX()) + ", " + format(pos.getZ())).setStyle(new Style().setColor(TextFormatting.GREEN)).appendSibling(new StringTextComponent(" using loot table: " + rl)), true);
          }
          i++;
        }
      }
      return 1;
    });
    return builder;
  }
}

