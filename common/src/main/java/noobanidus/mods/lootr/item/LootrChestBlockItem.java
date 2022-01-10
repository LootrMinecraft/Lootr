package noobanidus.mods.lootr.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

public class LootrChestBlockItem extends BlockItem {
  public LootrChestBlockItem(Block pBlock, Properties pProperties) {
    super(pBlock, pProperties);
  }

  // TODO:
/*  @Override
  public void initializeClient(Consumer<IItemRenderProperties> consumer) {
    consumer.accept(new IItemRenderProperties() {
      @Override
      public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
        return LootrChestItemRenderer.getInstance();
      }
    });
  }*/
}
