package noobanidus.mods.lootr;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.command.CommandLootr;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.init.ModAdvancements;
import noobanidus.mods.lootr.init.ModBlocks;

@Mod("lootr")
public class Lootr {

  static {
    ModAdvancements.load();
  }

  public CommandLootr COMMAND_LOOTR;
  public static CreativeModeTab TAB = new CreativeModeTab(LootrAPI.MODID) {
    @Override
    public ItemStack makeIcon() {
      return new ItemStack(ModBlocks.CHEST);
    }
  };

  public Lootr() {
    ModLoadingContext context = ModLoadingContext.get();
    context.registerConfig(ModConfig.Type.COMMON, ConfigManager.COMMON_CONFIG);
    context.registerConfig(ModConfig.Type.CLIENT, ConfigManager.CLIENT_CONFIG);
    ConfigManager.loadConfig(ConfigManager.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve(LootrAPI.MODID + "-common.toml"));
    if (ConfigManager.MAXIMUM_AGE.get() == ConfigManager.OLD_MAX_AGE) {
      ConfigManager.MAXIMUM_AGE.set(60 * 20 * 15);
      ConfigManager.COMMON_CONFIG.save();
    }
    ConfigManager.loadConfig(ConfigManager.CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve(LootrAPI.MODID + "-client.toml"));
    MinecraftForge.EVENT_BUS.addListener(this::onCommands);
  }

  public void onCommands(RegisterCommandsEvent event) {
    COMMAND_LOOTR = new CommandLootr(event.getDispatcher());
    COMMAND_LOOTR.register();
  }
}
