package noobanidus.mods.lootr.item;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import noobanidus.mods.lootr.client.item.LootrChestItemRenderer;

import java.util.function.Consumer;

public class LootrChestBlockItem extends BlockItem {
  public LootrChestBlockItem(Block pBlock, Properties pProperties) {
    super(pBlock, pProperties);
  }

  @Override
  public void initializeClient(Consumer<IClientItemExtensions> consumer) {
    consumer.accept(new IClientItemExtensions() {
      @Override
      public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return LootrChestItemRenderer.getInstance();
      }
    });
  }
}
