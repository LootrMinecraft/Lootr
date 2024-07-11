package noobanidus.mods.lootr.setup;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.api.registry.LootrRegistry;
import noobanidus.mods.lootr.block.entity.LootrChestBlockEntity;
import noobanidus.mods.lootr.block.entity.LootrShulkerBlockEntity;
import noobanidus.mods.lootr.block.entity.LootrTrappedChestBlockEntity;
import noobanidus.mods.lootr.client.block.BarrelModel;
import noobanidus.mods.lootr.client.block.LootrChestBlockRenderer;
import noobanidus.mods.lootr.client.block.LootrShulkerBlockRenderer;
import noobanidus.mods.lootr.client.entity.LootrChestCartRenderer;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;

@EventBusSubscriber(modid = LootrAPI.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientSetup {

  @SubscribeEvent
  public static void modelRegister(ModelEvent.RegisterGeometryLoaders event) {
    event.register(LootrAPI.rl("barrel"), BarrelModel.Loader.INSTANCE);
  }

  @SubscribeEvent
  public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
    event.registerBlockEntityRenderer((BlockEntityType<LootrChestBlockEntity>) LootrRegistry.getChestBlockEntity(), LootrChestBlockRenderer::new);
    event.registerBlockEntityRenderer((BlockEntityType<LootrTrappedChestBlockEntity>) LootrRegistry.getTrappedChestBlockEntity(), LootrChestBlockRenderer::new);
    event.registerBlockEntityRenderer((BlockEntityType<LootrChestBlockEntity>) LootrRegistry.getChestBlockEntity(), LootrChestBlockRenderer::new);
    event.registerBlockEntityRenderer((BlockEntityType<LootrShulkerBlockEntity>) LootrRegistry.getShulkerBlockEntity(), LootrShulkerBlockRenderer::new);
    event.registerEntityRenderer((EntityType<LootrChestMinecartEntity>) LootrRegistry.getMinecart(), (e) -> new LootrChestCartRenderer<>(e, ModelLayers.CHEST_MINECART));
  }
}
