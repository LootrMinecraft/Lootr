package noobanidus.mods.lootr;

import net.minecraft.world.item.CreativeModeTab;
import noobanidus.mods.lootr.command.CommandLootr;
import noobanidus.mods.lootr.init.ModAdvancements;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Lootr {
  public static final Logger LOG = LogManager.getLogger();
  public static final String MODID = "lootr";

  static {
    ModAdvancements.load();
  }

  public CommandLootr COMMAND_LOOTR;
  public static CreativeModeTab TAB = CreativeModeTab.TAB_MISC;
/*      new CreativeModeTab(MODID) {
    public ItemStack makeIcon() {
      return new ItemStack(ModBlocks.CHEST);
    }
  };*/

  public Lootr() {
/*    ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigManager.COMMON_CONFIG);
    ConfigManager.loadConfig(ConfigManager.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve(Lootr.MODID + "-common.toml"));
    MinecraftForge.EVENT_BUS.addListener(this::onCommands);*/
  }

/*  public void onCommands(RegisterCommandsEvent event) {
    COMMAND_LOOTR = new CommandLootr(event.getDispatcher());
    COMMAND_LOOTR.register();
  }*/

  public static void init () {
  }
}
