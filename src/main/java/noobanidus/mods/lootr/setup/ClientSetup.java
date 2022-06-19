package noobanidus.mods.lootr.setup;

import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
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
import noobanidus.mods.lootr.client.item.SpecialLootChestItemRenderer;
import noobanidus.mods.lootr.client.item.SpecialLootShulkerItemRenderer;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;
import noobanidus.mods.lootr.init.ModBlocks;
import noobanidus.mods.lootr.init.ModEntities;
import noobanidus.mods.lootr.init.ModItems;
import noobanidus.mods.lootr.init.ModTiles;

@Mod.EventBusSubscriber(modid = Lootr.MODID, value = Side.CLIENT)
public class ClientSetup extends CommonSetup {
  public void preInit() {
    super.preInit();
    ClientRegistry.bindTileEntitySpecialRenderer(LootrChestTileEntity.class, new SpecialLootChestTileRenderer<>());
    ClientRegistry.bindTileEntitySpecialRenderer(TrappedLootrChestTileEntity.class, new SpecialLootChestTileRenderer<>());
    ClientRegistry.bindTileEntitySpecialRenderer(LootrShulkerTileEntity.class, new SpecialLootShulkerTileRenderer());
    RenderingRegistry.registerEntityRenderingHandler(LootrChestMinecartEntity.class, LootrMinecartRenderer::new);
  }

  @Override
  public void init() {
    super.init();
    ModItems.CHEST.setTileEntityItemStackRenderer(new SpecialLootChestItemRenderer());
    ModItems.TRAPPED_CHEST.setTileEntityItemStackRenderer(new SpecialLootChestItemRenderer());
    ModItems.SHULKER.setTileEntityItemStackRenderer(new SpecialLootShulkerItemRenderer());
  }

    @SubscribeEvent
  public static void registerModels(ModelRegistryEvent event) {
      ModelLoader.setCustomStateMapper(ModBlocks.CHEST, new StateMap.Builder().ignore(BlockChest.FACING).build());
      ModelLoader.setCustomStateMapper(ModBlocks.TRAPPED_CHEST, new StateMap.Builder().ignore(BlockChest.FACING).build());
      ModelLoader.setCustomStateMapper(ModBlocks.SHULKER, new StateMap.Builder().ignore(BlockShulkerBox.FACING).build());
      ModelLoader.setCustomModelResourceLocation(ModItems.CHEST, 0, new ModelResourceLocation(new ResourceLocation(Lootr.MODID, "lootr_chest_item"), null));
      ModelLoader.setCustomModelResourceLocation(ModItems.SHULKER, 0, new ModelResourceLocation(new ResourceLocation(Lootr.MODID, "lootr_shulker_item"), null));
      ModelLoader.setCustomModelResourceLocation(ModItems.TRAPPED_CHEST, 0, new ModelResourceLocation(new ResourceLocation(Lootr.MODID, "lootr_trapped_chest_item"), null));
      ModelLoader.setCustomModelResourceLocation(ModItems.TROPHY, 0, new ModelResourceLocation(new ResourceLocation(Lootr.MODID, "trophy_item"), null));
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

    @Override
    public void changeCartStatus(int entityId, boolean status) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            World world = Minecraft.getMinecraft().world;
            if (world == null) {
                Lootr.LOG.info("Unable to mark entity with id '" + entityId + "' as opened as world is null.");
                return;
            }
            Entity cart = world.getEntityByID(entityId);
            if (cart == null) {
                Lootr.LOG.info("Unable to mark entity with id '" + entityId + "' as opened as entity is null.");
                return;
            }

            if (!(cart instanceof LootrChestMinecartEntity)) {
                Lootr.LOG.info("Unable to mark entity with id '" + entityId + "' as opened as entity is not a Lootr minecart.");
                return;
            }

            if(status)
                ((LootrChestMinecartEntity) cart).setOpened();
            else
                ((LootrChestMinecartEntity) cart).setClosed();
        });
    }
}
