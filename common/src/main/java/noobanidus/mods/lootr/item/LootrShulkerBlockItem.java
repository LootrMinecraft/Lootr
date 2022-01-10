package noobanidus.mods.lootr.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

public class LootrShulkerBlockItem extends BlockItem {
  public LootrShulkerBlockItem(Block pBlock, Properties pProperties) {
    super(pBlock, pProperties);
  }

  // TODO:
/*  @Override
  public void initializeClient(Consumer<IItemRenderProperties> consumer) {
    consumer.accept(new IItemRenderProperties() {
      @Override
      public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
        return LootrShulkerItemRenderer.getInstance();
      }
    });
  }*/
}
