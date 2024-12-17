package noobanidus.mods.lootr.neoforge.setup;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;
import noobanidus.mods.lootr.common.block.entity.LootrChestBlockEntity;
import noobanidus.mods.lootr.common.block.entity.LootrInventoryBlockEntity;
import noobanidus.mods.lootr.common.block.entity.LootrShulkerBlockEntity;
import noobanidus.mods.lootr.common.block.entity.LootrTrappedChestBlockEntity;
import noobanidus.mods.lootr.common.client.block.LootrChestBlockRenderer;
import noobanidus.mods.lootr.common.client.block.LootrShulkerBlockRenderer;
import noobanidus.mods.lootr.common.client.entity.LootrChestCartRenderer;
import noobanidus.mods.lootr.common.client.special.LootrChestSpecialRenderer;
import noobanidus.mods.lootr.common.client.special.LootrShulkerSpecialRenderer;
import noobanidus.mods.lootr.common.entity.LootrChestMinecartEntity;
import noobanidus.mods.lootr.neoforge.client.block.BarrelModelLoader;

@EventBusSubscriber(modid = LootrAPI.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientSetup {

  @SubscribeEvent
  public static void modelRegister(ModelEvent.RegisterLoaders event) {
    event.register(LootrAPI.rl("barrel"), BarrelModelLoader.getInstance());
  }

  @SuppressWarnings("unchecked")
  @SubscribeEvent
  public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
    event.registerBlockEntityRenderer((BlockEntityType<LootrChestBlockEntity>) LootrRegistry.getChestBlockEntity(), LootrChestBlockRenderer::new);
    event.registerBlockEntityRenderer((BlockEntityType<LootrTrappedChestBlockEntity>) LootrRegistry.getTrappedChestBlockEntity(), LootrChestBlockRenderer::new);
    event.registerBlockEntityRenderer((BlockEntityType<LootrChestBlockEntity>) LootrRegistry.getChestBlockEntity(), LootrChestBlockRenderer::new);
    event.registerBlockEntityRenderer((BlockEntityType<LootrShulkerBlockEntity>) LootrRegistry.getShulkerBlockEntity(), LootrShulkerBlockRenderer::new);
    event.registerBlockEntityRenderer((BlockEntityType<LootrInventoryBlockEntity>) LootrRegistry.getInventoryBlockEntity(), LootrChestBlockRenderer::new);
    event.registerEntityRenderer((EntityType<LootrChestMinecartEntity>) LootrRegistry.getMinecart(), (e) -> new LootrChestCartRenderer<>(e, ModelLayers.CHEST_MINECART));
  }

  @SubscribeEvent
  public static void registerSpecialModels(RegisterSpecialModelRendererEvent event) {
    event.register(LootrAPI.rl("chest"), LootrChestSpecialRenderer.Unbaked.MAP_CODEC);
    event.register(LootrAPI.rl("shulker"), LootrShulkerSpecialRenderer.Unbaked.MAP_CODEC);
  }
}
