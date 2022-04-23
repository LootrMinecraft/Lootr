package noobanidus.mods.lootr.init;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.block.tile.*;

@Mod.EventBusSubscriber(modid = Lootr.MODID)
public class ModTiles {
  @SubscribeEvent
  public static void registerTileEntityType(RegistryEvent.Register<Block> event) {
    GameRegistry.registerTileEntity(LootrChestTileEntity.class, new ResourceLocation(Lootr.MODID, "special_loot_chest"));
    GameRegistry.registerTileEntity(TrappedLootrChestTileEntity.class, new ResourceLocation(Lootr.MODID, "special_trapped_loot_chest"));
    GameRegistry.registerTileEntity(LootrShulkerTileEntity.class, new ResourceLocation(Lootr.MODID, "special_loot_shulker"));
  }
}
