package noobanidus.mods.lootr.fabric.impl.models;

import com.google.auto.service.AutoService;
import noobanidus.mods.lootr.common.api.client.ILootrFabricModelProvider;

@AutoService(ILootrFabricModelProvider.class)
public class FabricBrushablesProvider implements ILootrFabricModelProvider {
  @Override
  public void provideModels(Acceptor acceptor) {
    acceptor.acceptBrushableModel(BrushableLocations.SUSPICIOUS_SAND, BrushableLocations.SAND_OPENED, BrushableLocations.SAND_STAGE_0, BrushableLocations.SAND_STAGE_1, BrushableLocations.SAND_STAGE_2, BrushableLocations.SAND_STAGE_3);
    acceptor.acceptBrushableModel(BrushableLocations.SUSPICIOUS_GRAVEL, BrushableLocations.GRAVEL_OPENED, BrushableLocations.GRAVEL_STAGE_0, BrushableLocations.GRAVEL_STAGE_1, BrushableLocations.GRAVEL_STAGE_2, BrushableLocations.GRAVEL_STAGE_3);
  }
}
