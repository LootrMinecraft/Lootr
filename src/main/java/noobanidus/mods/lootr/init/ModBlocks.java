package noobanidus.mods.lootr.init;

import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.block.*;

import java.util.Set;

@Mod.EventBusSubscriber(modid = Lootr.MODID)
public class ModBlocks {
  public static final BlockChest.Type TYPE_LOOTR = EnumHelper.addEnum(BlockChest.Type.class, "LOOTR", new Class[0]);
  public static final BlockChest.Type TYPE_LOOTR_TRAP = EnumHelper.addEnum(BlockChest.Type.class, "LOOTR_TRAP", new Class[0]);
  public static LootrChestBlock CHEST = (LootrChestBlock) new LootrChestBlock(TYPE_LOOTR, false).setHardness(2.5F);
  public static LootrChestBlock INVENTORY = (LootrChestBlock) new LootrChestBlock(TYPE_LOOTR, true).setHardness(2.5F);

  public static LootrChestBlock TRAPPED_CHEST = (LootrChestBlock) new LootrChestBlock(TYPE_LOOTR_TRAP, false).setHardness(2.5F);


  public static LootrShulkerBlock SHULKER = (LootrShulkerBlock) new LootrShulkerBlock().setHardness(2.5f);
  public static Set<Block> LOOT_CONTAINERS = Sets.newHashSet(CHEST, TRAPPED_CHEST, SHULKER, INVENTORY);

  public static Block TROPHY = new TrophyBlock().setHardness(15f);

  static {
    CHEST.setRegistryName(Lootr.MODID, "lootr_chest");
    INVENTORY.setRegistryName(Lootr.MODID, "lootr_inventory");
    TRAPPED_CHEST.setRegistryName(Lootr.MODID, "lootr_trapped_chest");
    SHULKER.setRegistryName(Lootr.MODID, "lootr_shulker");
  }

  @SubscribeEvent
  public static void registerBlocks(RegistryEvent.Register<Block> event) {
    LOOT_CONTAINERS.forEach(o -> event.getRegistry().register(o));
    event.getRegistry().register(TROPHY);
  }
}
