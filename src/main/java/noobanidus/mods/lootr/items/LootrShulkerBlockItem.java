package noobanidus.mods.lootr.items;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.IItemRenderProperties;
import noobanidus.mods.lootr.client.LootrShulkerBlockRenderer;
import noobanidus.mods.lootr.client.LootrShulkerItemRenderer;

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
