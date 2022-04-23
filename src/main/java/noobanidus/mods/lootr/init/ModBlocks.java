package noobanidus.mods.lootr.init;

import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.block.*;

import java.util.Set;

@Mod.EventBusSubscriber(modid = Lootr.MODID)
public class ModBlocks {

  public static LootrChestBlock CHEST = (LootrChestBlock) new LootrChestBlock().setHardness(2.5F);

  public static LootrTrappedChestBlock TRAPPED_CHEST = (LootrTrappedChestBlock) new LootrTrappedChestBlock().setHardness(2.5F);


  public static LootrShulkerBlock SHULKER = (LootrShulkerBlock) new LootrShulkerBlock().setHardness(2.5f);
  public static Set<Block> LOOT_CONTAINERS = Sets.newHashSet(CHEST, TRAPPED_CHEST, SHULKER);

  public static Block TROPHY = new TrophyBlock().setHardness(15f);

  static {
    CHEST.setRegistryName(Lootr.MODID, "lootr_chest");
    TRAPPED_CHEST.setRegistryName(Lootr.MODID, "lootr_trapped_chest");
    SHULKER.setRegistryName(Lootr.MODID, "lootr_shulker");
  }

  @SubscribeEvent
  public static void registerBlocks(RegistryEvent.Register<Block> event) {
    LOOT_CONTAINERS.forEach(o -> event.getRegistry().register(o));
    event.getRegistry().register(TROPHY);
  }
}
