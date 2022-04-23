package noobanidus.mods.lootr.setup;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.block.tile.LootrChestTileEntity;
import noobanidus.mods.lootr.block.tile.LootrShulkerTileEntity;
import noobanidus.mods.lootr.block.tile.TrappedLootrChestTileEntity;
import noobanidus.mods.lootr.client.ClientGetter;
import noobanidus.mods.lootr.client.block.SpecialLootChestTileRenderer;
import noobanidus.mods.lootr.client.block.SpecialLootShulkerTileRenderer;
import noobanidus.mods.lootr.client.entity.LootrMinecartRenderer;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;
import noobanidus.mods.lootr.init.ModEntities;
import noobanidus.mods.lootr.init.ModTiles;

@Mod.EventBusSubscriber(modid = Lootr.MODID, value = Side.CLIENT)
public class ClientSetup extends CommonSetup {
  public void preInit() {
    ClientRegistry.bindTileEntitySpecialRenderer(LootrChestTileEntity.class, new SpecialLootChestTileRenderer<>());
    ClientRegistry.bindTileEntitySpecialRenderer(TrappedLootrChestTileEntity.class, new SpecialLootChestTileRenderer<>());
    ClientRegistry.bindTileEntitySpecialRenderer(LootrShulkerTileEntity.class, new SpecialLootShulkerTileRenderer());
    RenderingRegistry.registerEntityRenderingHandler(LootrChestMinecartEntity.class, LootrMinecartRenderer::new);
  }

    @Override
    public EntityPlayer getPlayer() {
        return ClientGetter.getPlayer();
    }

    @SubscribeEvent
  public static void stitch(TextureStitchEvent.Pre event) {
      event.getMap().registerSprite(new ResourceLocation(Lootr.MODID, "shulker"));
      event.getMap().registerSprite(new ResourceLocation(Lootr.MODID, "chest"));
      event.getMap().registerSprite(new ResourceLocation(Lootr.MODID, "shulker_opened"));
      event.getMap().registerSprite(new ResourceLocation(Lootr.MODID, "chest_opened"));
  }
}
