package noobanidus.mods.lootr;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.events.HandleBreak;
import noobanidus.mods.lootr.init.ModTiles;
import noobanidus.mods.lootr.setup.ClientSetup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("lootr")
public class Lootr {
  public static final Logger LOG = LogManager.getLogger();
  public static final String MODID = "lootr";

  public Lootr() {
    ConfigManager.loadConfig(ConfigManager.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve(Lootr.MODID + "-common.toml"));
    IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
    MinecraftForge.EVENT_BUS.addListener(HandleBreak::onBlockBreak);

    DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
      modBus.addListener(ClientSetup::init);
    });

    modBus.addGenericListener(TileEntityType.class, ModTiles::registerTileEntityType);
  }
}
