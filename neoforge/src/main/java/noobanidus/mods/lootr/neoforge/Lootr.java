package noobanidus.mods.lootr.neoforge;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.PlatformAPI;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;
import noobanidus.mods.lootr.common.command.CommandLootr;
import noobanidus.mods.lootr.neoforge.config.ConfigManager;
import noobanidus.mods.lootr.neoforge.impl.LootrAPIImpl;
import noobanidus.mods.lootr.neoforge.impl.LootrRegistryImpl;
import noobanidus.mods.lootr.neoforge.impl.PlatformAPIImpl;
import noobanidus.mods.lootr.neoforge.init.*;

// TODO: Ideas/important things to be implemented
// - Display notices after containers are closed
// - or use toasts
// - lockouts?
@Mod(value = LootrAPI.MODID)
public class Lootr {
  public Lootr(ModContainer modContainer, IEventBus modBus) {
    LootrAPI.INSTANCE = new LootrAPIImpl();
    LootrRegistry.INSTANCE = new LootrRegistryImpl();
    PlatformAPI.INSTANCE = new PlatformAPIImpl();

    modContainer.registerConfig(ModConfig.Type.COMMON, ConfigManager.COMMON_CONFIG);
    modContainer.registerConfig(ModConfig.Type.CLIENT, ConfigManager.CLIENT_CONFIG);
    NeoForge.EVENT_BUS.addListener(this::onCommands);
    ModTabs.register(modBus);
    ModBlockEntities.register(modBus);
    ModBlocks.register(modBus);
    ModEntities.register(modBus);
    ModItems.register(modBus);
    ModLoot.register(modBus);
    ModStats.register(modBus);
    ModAdvancements.register(modBus);
  }

  public static ResourceLocation rl(String path) {
    return LootrAPI.rl(path);
  }

  public void onCommands(RegisterCommandsEvent event) {
    CommandLootr.register(event.getDispatcher());
  }
}
