package noobanidus.mods.lootr;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import noobanidus.mods.lootr.advancement.GenericTrigger;
import noobanidus.mods.lootr.commands.CommandLootr;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.events.HandleAdvancement;
import noobanidus.mods.lootr.events.HandleBreak;
import noobanidus.mods.lootr.events.HandleCart;
import noobanidus.mods.lootr.gen.LootrBlockTagGenerator;
import noobanidus.mods.lootr.init.ModBlocks;
import noobanidus.mods.lootr.setup.Setup;
import noobanidus.mods.lootr.ticker.EntityTicker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

@Mod("lootr")
public class Lootr {
  public static final Logger LOG = LogManager.getLogger();
  public static final String MODID = "lootr";
  public static GenericTrigger<UUID> CHEST_PREDICATE = null;
  public static GenericTrigger<UUID> BARREL_PREDICATE = null;
  public static GenericTrigger<UUID> CART_PREDICATE = null;
  public static GenericTrigger<UUID> SHULKER_PREDICATE = null;
  public static GenericTrigger<Void> SCORE_PREDICATE = null;
  public static GenericTrigger<ResourceLocation> ADVANCEMENT_PREDICATE = null;
  public static final ResourceLocation CHEST_LOCATION = new ResourceLocation(MODID, "chest_opened");
  public static final ResourceLocation BARREL_LOCATION = new ResourceLocation(MODID, "barrel_opened");
  public static final ResourceLocation CART_LOCATION = new ResourceLocation(MODID, "cart_opened");
  public static final ResourceLocation SHULKER_LOCATION = new ResourceLocation(MODID, "shulker_opened");
  public static final ResourceLocation ADVANCEMENT_LOCATION = new ResourceLocation(MODID, "advancement");
  public static final ResourceLocation SCORE_LOCATION = new ResourceLocation(MODID, "score");
  public CommandLootr COMMAND_LOOTR;
  public static CreativeModeTab TAB = new CreativeModeTab(MODID) {
    @Override
    @OnlyIn(Dist.CLIENT)
    public ItemStack makeIcon() {
      return new ItemStack(ModBlocks.CHEST);
    }
  };

  public Lootr() {
    ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigManager.COMMON_CONFIG);
    ConfigManager.loadConfig(ConfigManager.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve(Lootr.MODID + "-common.toml"));
    MinecraftForge.EVENT_BUS.addListener(HandleBreak::onBlockBreak);
    MinecraftForge.EVENT_BUS.addListener(HandleCart::onEntityJoin);
    MinecraftForge.EVENT_BUS.addListener(HandleCart::onEntityTrack);
    MinecraftForge.EVENT_BUS.addListener(HandleAdvancement::onAdvancement);
    MinecraftForge.EVENT_BUS.addListener(EntityTicker::onServerTick);
    MinecraftForge.EVENT_BUS.addListener(this::onCommands);


    DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> Setup::client);
  }

  public void onCommands(RegisterCommandsEvent event) {
    COMMAND_LOOTR = new CommandLootr(event.getDispatcher());
    COMMAND_LOOTR.register();
  }
}
