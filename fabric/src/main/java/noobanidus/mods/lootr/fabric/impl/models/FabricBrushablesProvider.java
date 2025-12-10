package noobanidus.mods.lootr.fabric.impl.models;

import com.google.auto.service.AutoService;
import noobanidus.mods.lootr.common.api.client.ILootrFabricModelProvider;
import noobanidus.mods.lootr.fabric.client.block.BrushableModelLoader;

@AutoService(ILootrFabricModelProvider.class)
public class FabricBrushablesProvider implements ILootrFabricModelProvider {
  @Override
  public void provideModels(Acceptor acceptor) {
    acceptor.acceptBrushableModel(BrushableModelLoader.SUSPICIOUS_SAND, BrushableModelLoader.SAND_OPENED, BrushableModelLoader.SAND_STAGE_0, BrushableModelLoader.SAND_STAGE_1, BrushableModelLoader.SAND_STAGE_2, BrushableModelLoader.SAND_STAGE_3);
    acceptor.acceptBrushableModel(BrushableModelLoader.SUSPICIOUS_GRAVEL, BrushableModelLoader.GRAVEL_OPENED, BrushableModelLoader.GRAVEL_STAGE_0, BrushableModelLoader.GRAVEL_STAGE_1, BrushableModelLoader.GRAVEL_STAGE_2, BrushableModelLoader.GRAVEL_STAGE_3);
  }
}
