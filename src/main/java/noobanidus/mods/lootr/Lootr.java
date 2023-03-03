package noobanidus.mods.lootr;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import noobanidus.mods.lootr.api.IServerAccess;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.command.CommandLootr.CommandLootr;
import noobanidus.mods.lootr.init.*;
import org.jetbrains.annotations.Nullable;

public class Lootr implements ModInitializer {
  public static IServerAccess serverAccess = new IServerAccess() {
    private MinecraftServer server = null;
    @Override
    public @Nullable MinecraftServer getServer() {
      return server;
    }

    @Override
    public void setServer(MinecraftServer server) {
      this.server = server;
    }
  };

  public static CreativeModeTab TAB = FabricItemGroupBuilder.build(new ResourceLocation(LootrAPI.MODID, LootrAPI.MODID), () -> new ItemStack(ModItems.CHEST));

  @Override
  public void onInitialize() {
    ModConfig.register();
    ModItems.register();
    ModBlocks.register();
    ModBlockEntities.register();
    ModEntities.register();
    ModLoot.register();
    ModEvents.register();
    ModStats.register();
    ModAdvancements.register();

  }
}
