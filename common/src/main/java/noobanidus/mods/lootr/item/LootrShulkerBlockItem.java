package noobanidus.mods.lootr.item;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.IItemRenderProperties;
import noobanidus.mods.lootr.client.item.LootrShulkerItemRenderer;

import java.util.function.Consumer;

public class LootrShulkerBlockItem extends BlockItem {
  public LootrShulkerBlockItem(Block pBlock, Properties pProperties) {
    super(pBlock, pProperties);
  }

  @Override
  public void initializeClient(Consumer<IItemRenderProperties> consumer) {
    consumer.accept(new IItemRenderProperties() {
      @Override
      public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
        return LootrShulkerItemRenderer.getInstance();
      }
    });
  }
}
