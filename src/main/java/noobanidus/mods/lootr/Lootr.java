package noobanidus.mods.lootr;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.command.CommandLootr;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.init.*;
import noobanidus.mods.lootr.network.PacketHandler;

@Mod("lootr")
public class Lootr {
  public static Lootr instance;

  public static final String VERSION = "0.8";

  private final PacketHandler packetHandler;

  public CommandLootr COMMAND_LOOTR;

  public CreativeModeTab TAB;

  public static ResourceLocation rl (String path) {
    return new ResourceLocation(LootrAPI.MODID, path);
  }

  public Lootr(ModContainer modContainer, IEventBus modBus) {
    instance = this;
    ModLoadingContext context = ModLoadingContext.get();
    context.registerConfig(ModConfig.Type.COMMON, ConfigManager.COMMON_CONFIG);
    context.registerConfig(ModConfig.Type.CLIENT, ConfigManager.CLIENT_CONFIG);
    ConfigManager.loadConfig(ConfigManager.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve(LootrAPI.MODID + "-common.toml"));
    ConfigManager.loadConfig(ConfigManager.CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve(LootrAPI.MODID + "-client.toml"));
    NeoForge.EVENT_BUS.addListener(this::onCommands);
    ModTabs.register(modBus);
    ModBlockEntities.register(modBus);
    ModBlocks.register(modBus);
    ModEntities.register(modBus);
    ModItems.register(modBus);
    ModLoot.register(modBus);
    ModStats.register(modBus);
    ModAdvancements.register(modBus);
    this.packetHandler = new PacketHandler(modBus, LootrAPI.MODID, VERSION);
  }

  public void onCommands(RegisterCommandsEvent event) {
    COMMAND_LOOTR = new CommandLootr(event.getDispatcher());
    COMMAND_LOOTR.register();
  }

  public static PacketHandler getPacketHandler () {
    return instance.packetHandler;
  }
}
