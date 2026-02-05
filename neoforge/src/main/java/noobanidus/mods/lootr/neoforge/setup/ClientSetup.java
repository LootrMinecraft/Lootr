package noobanidus.mods.lootr.neoforge.setup;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.model.standalone.SimpleUnbakedStandaloneModel;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;
import noobanidus.mods.lootr.common.block.entity.*;
import noobanidus.mods.lootr.common.client.block.LootrBrushableBlockRenderer;
import noobanidus.mods.lootr.common.client.block.LootrChestBlockRenderer;
import noobanidus.mods.lootr.common.client.block.LootrDecoratedPotRenderer;
import noobanidus.mods.lootr.common.client.block.LootrShulkerBoxRenderer;
import noobanidus.mods.lootr.common.client.entity.LootrBlockStateDefinitions;
import noobanidus.mods.lootr.common.client.entity.LootrChestCartRenderer;
import noobanidus.mods.lootr.common.client.entity.LootrItemFrameRenderer;
import noobanidus.mods.lootr.common.client.particle.UnopenedParticle;
import noobanidus.mods.lootr.common.client.select.SelectConfigType;
import noobanidus.mods.lootr.common.client.special.LootrChestSpecialRenderer;
import noobanidus.mods.lootr.common.client.special.LootrDecoratedPotSpecialRenderer;
import noobanidus.mods.lootr.common.client.special.LootrShulkerSpecialRenderer;
import noobanidus.mods.lootr.common.entity.LootrChestMinecartEntity;
import noobanidus.mods.lootr.common.entity.LootrItemFrame;
import noobanidus.mods.lootr.neoforge.client.block.UnbakedBrushableModel;
import noobanidus.mods.lootr.neoforge.client.block.UnbakedCustomModel;
import noobanidus.mods.lootr.neoforge.init.ModParticles;

@EventBusSubscriber(modid = LootrAPI.MODID, value = Dist.CLIENT)
public class ClientSetup {
  public static final StandaloneModelKey<BlockStateModel> ITEM_FRAME = new StandaloneModelKey<>(LootrBlockStateDefinitions.LOOTR_ITEM_FRAME_LOCATION::toString);
  public static final StandaloneModelKey<BlockStateModel> ITEM_FRAME_OPEN = new StandaloneModelKey<>(LootrBlockStateDefinitions.LOOTR_OPEN_ITEM_FRAME_LOCATION::toString);

  @SubscribeEvent
  public static void modelAdditional(ModelEvent.RegisterStandalone event) {
    event.register(ITEM_FRAME, SimpleUnbakedStandaloneModel.blockStateModel(LootrBlockStateDefinitions.LOOTR_ITEM_FRAME_LOCATION.withPrefix("block/")));
    event.register(ITEM_FRAME_OPEN, SimpleUnbakedStandaloneModel.blockStateModel(LootrBlockStateDefinitions.LOOTR_OPEN_ITEM_FRAME_LOCATION.withPrefix("block/")));
  }

  @SubscribeEvent
  public static void registerSpecial (RegisterSpecialModelRendererEvent event) {
    event.register(LootrAPI.rl("chest"), LootrChestSpecialRenderer.Unbaked.MAP_CODEC);
    event.register(LootrAPI.rl("shulker_box"), LootrShulkerSpecialRenderer.Unbaked.MAP_CODEC);
    event.register(LootrAPI.rl("decorated_pot"), LootrDecoratedPotSpecialRenderer.Unbaked.MAP_CODEC);
  }

  @SubscribeEvent
  public static void modelRegister(RegisterBlockStateModels event) {
    // TODO: These should be constantified
    event.registerModel(LootrAPI.rl("custom"), UnbakedCustomModel.CODEC);
    event.registerModel(LootrAPI.rl("brushable"), UnbakedBrushableModel.CODEC);
  }

  @SuppressWarnings("unchecked")
  @SubscribeEvent
  public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
    event.registerBlockEntityRenderer((BlockEntityType<LootrChestBlockEntity>) LootrRegistry.getChestBlockEntity(), LootrChestBlockRenderer::new);
    event.registerBlockEntityRenderer((BlockEntityType<LootrTrappedChestBlockEntity>) LootrRegistry.getTrappedChestBlockEntity(), LootrChestBlockRenderer::new);
    event.registerBlockEntityRenderer((BlockEntityType<LootrChestBlockEntity>) LootrRegistry.getChestBlockEntity(), LootrChestBlockRenderer::new);
    event.registerBlockEntityRenderer((BlockEntityType<LootrShulkerBlockEntity>) LootrRegistry.getShulkerBlockEntity(), LootrShulkerBoxRenderer::new);
    event.registerBlockEntityRenderer((BlockEntityType<LootrInventoryBlockEntity>) LootrRegistry.getInventoryBlockEntity(), LootrChestBlockRenderer::new);
    event.registerEntityRenderer((EntityType<LootrChestMinecartEntity>) LootrRegistry.getMinecart(), (e) -> new LootrChestCartRenderer<>(e, ModelLayers.CHEST_MINECART));
    event.registerBlockEntityRenderer((BlockEntityType<LootrBrushableBlockEntity>) LootrRegistry.getBrushableBlockEntity(), LootrBrushableBlockRenderer::new);
    event.registerBlockEntityRenderer((BlockEntityType<LootrDecoratedPotBlockEntity>) LootrRegistry.getDecoratedPotBlockEntity(), LootrDecoratedPotRenderer::new);
    event.registerEntityRenderer((EntityType<LootrItemFrame>) LootrRegistry.getItemFrame(), LootrItemFrameRenderer::new);
  }

  @SubscribeEvent
  public static void registerLayersEvent(EntityRenderersEvent.RegisterLayerDefinitions event) {
    event.registerLayerDefinition(LootrDecoratedPotRenderer.OPEN_POT_LAYER, LootrDecoratedPotRenderer::createBodyLayer);
  }

  @SubscribeEvent
  public static void registerParticles(RegisterParticleProvidersEvent event) {
    event.registerSpriteSet(ModParticles.UNOPENED_PARTICLE.get(), UnopenedParticle.Provider::new);
  }

  @SubscribeEvent
  public static void registerSelect (RegisterSelectItemModelPropertyEvent event) {
    event.register(LootrAPI.rl("config_type"), SelectConfigType.TYPE);
  }
}
