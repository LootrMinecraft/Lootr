package noobanidus.mods.lootr;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noobanidus.mods.lootr.init.ModBlocks;
import noobanidus.mods.lootr.setup.CommonSetup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = "lootr")
public class Lootr {
  public static final boolean CONFIG_RANDOMIZE_SEED = true;
  public static final Logger LOG = LogManager.getLogger();
  public static final String MODID = "lootr";

  @Mod.Instance("lootr")
  public static Lootr instance;

  @SidedProxy(clientSide = "noobanidus.mods.lootr.setup.ClientSetup", serverSide = "noobanidus.mods.lootr.setup.CommonSetup")
  public static CommonSetup proxy;


  @Mod.EventHandler
  private void preInit(FMLPreInitializationEvent event) {
    proxy.preInit();
  }

  @Mod.EventHandler
  private void init(FMLInitializationEvent event) {
    proxy.init();
  }

  public static CreativeTabs TAB = new CreativeTabs(MODID) {
    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack createIcon() {
      return new ItemStack(ModBlocks.CHEST);
    }
  };

  public Lootr() {
    /*
    ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigManager.COMMON_CONFIG);
    ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigManager.CLIENT_CONFIG);
    ConfigManager.loadConfig(ConfigManager.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve(Lootr.MODID + "-common.toml"));
    ConfigManager.loadConfig(ConfigManager.CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve(Lootr.MODID + "-client.toml"));
     */
  }
}
