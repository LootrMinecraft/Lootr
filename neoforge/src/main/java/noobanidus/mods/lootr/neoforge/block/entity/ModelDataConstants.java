package noobanidus.mods.lootr.neoforge.block.entity;

import net.neoforged.neoforge.client.model.data.ModelData;
import noobanidus.mods.lootr.neoforge.init.ModBlockProperties;

public class ModelDataConstants {
  public static final ModelData OPENED_MODEL_DATA = ModelData.builder().with(ModBlockProperties.OPENED, true).build();
  public static final ModelData CLOSED_MODEL_DATA = ModelData.builder().with(ModBlockProperties.OPENED, false).build();
}
