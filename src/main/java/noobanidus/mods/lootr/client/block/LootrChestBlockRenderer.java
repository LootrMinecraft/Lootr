package noobanidus.mods.lootr.client.block;

import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.state.properties.ChestType;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.api.registry.LootrRegistry;
import noobanidus.mods.lootr.block.entity.LootrChestBlockEntity;

@SuppressWarnings({"NullableProblems", "deprecation"})
public class LootrChestBlockRenderer<T extends LootrChestBlockEntity & ILootrBlockEntity> extends ChestRenderer<T> {
  public static final Material MATERIAL = new Material(Sheets.CHEST_SHEET, LootrAPI.rl("chest"));
  public static final Material MATERIAL2 = new Material(Sheets.CHEST_SHEET, LootrAPI.rl("chest_opened"));
  public static final Material MATERIAL3 = new Material(Sheets.CHEST_SHEET, LootrAPI.rl("chest_trapped"));
  public static final Material MATERIAL4 = new Material(Sheets.CHEST_SHEET, LootrAPI.rl("chest_trapped_opened"));
  public static final Material OLD_MATERIAL = new Material(Sheets.CHEST_SHEET, LootrAPI.rl("old_chest"));
  public static final Material OLD_MATERIAL2 = new Material(Sheets.CHEST_SHEET, LootrAPI.rl("old_chest_opened"));

  public LootrChestBlockRenderer(BlockEntityRendererProvider.Context p_173607_) {
    super(p_173607_);
  }

  @Override
  protected Material getMaterial(T blockEntity, ChestType type) {
    if (LootrAPI.isVanillaTextures()) {
      return Sheets.chooseMaterial(blockEntity, type, false);
    }
    // TODO: ???
    boolean trapped = blockEntity.getType().equals(LootrRegistry.getTrappedChestBlockEntity());
    if (blockEntity.isClientOpened()) {
      if (LootrAPI.isOldTextures()) {
        return OLD_MATERIAL2;
      }
      return trapped ? MATERIAL4 : MATERIAL2;
    } else {
      if (LootrAPI.isOldTextures()) {
        return OLD_MATERIAL;
      }
      return trapped ? MATERIAL3 : MATERIAL;
    }
  }
}
