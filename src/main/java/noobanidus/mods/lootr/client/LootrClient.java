package noobanidus.mods.lootr.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.model.geom.ModelLayers;
import noobanidus.mods.lootr.client.block.BarrelModel;
import noobanidus.mods.lootr.client.block.LootrChestBlockRenderer;
import noobanidus.mods.lootr.client.block.LootrShulkerBlockRenderer;
import noobanidus.mods.lootr.client.entity.LootrChestCartRenderer;
import noobanidus.mods.lootr.client.item.LootrChestItemRenderer;
import noobanidus.mods.lootr.client.item.LootrShulkerItemRenderer;
import noobanidus.mods.lootr.init.ModBlockEntities;
import noobanidus.mods.lootr.init.ModBlocks;
import noobanidus.mods.lootr.init.ModEntities;
import noobanidus.mods.lootr.network.Networking;

@Environment(EnvType.CLIENT)
public class LootrClient implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    Networking.register();

    BlockEntityRendererRegistry.register(ModBlockEntities.SPECIAL_LOOT_CHEST, LootrChestBlockRenderer::new);
    BuiltinItemRendererRegistry.INSTANCE.register(ModBlocks.CHEST, LootrChestItemRenderer.getInstance());

    BlockEntityRendererRegistry.register(ModBlockEntities.SPECIAL_TRAPPED_LOOT_CHEST, LootrChestBlockRenderer::new);
    BuiltinItemRendererRegistry.INSTANCE.register(ModBlocks.TRAPPED_CHEST, LootrChestItemRenderer.getInstance());

    BlockEntityRendererRegistry.register(ModBlockEntities.SPECIAL_LOOT_INVENTORY, LootrChestBlockRenderer::new);
    BuiltinItemRendererRegistry.INSTANCE.register(ModBlocks.INVENTORY, LootrChestItemRenderer.getInstance());

    BlockEntityRendererRegistry.register(ModBlockEntities.SPECIAL_LOOT_SHULKER, LootrShulkerBlockRenderer::new);
    BuiltinItemRendererRegistry.INSTANCE.register(ModBlocks.SHULKER, LootrShulkerItemRenderer.getInstance());

    ModelLoadingRegistry.INSTANCE.registerResourceProvider(o -> new BarrelModel.BarrelModelLoader());

    EntityRendererRegistry.register(ModEntities.LOOTR_MINECART_ENTITY, (context) -> new LootrChestCartRenderer<>(context, ModelLayers.CHEST_MINECART));
  }
}
