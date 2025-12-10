package noobanidus.mods.lootr.common.api.client;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public interface ILootrFabricModelProvider {
  void provideModels(Acceptor acceptor);

  interface Acceptor {
    void acceptBarrelModel (ResourceLocation modelName, ResourceLocation modelOpenedLocation, ResourceLocation modelUnopenedLocation, @Nullable ResourceLocation modelVanillaLocation);

    void acceptBrushableModel (ResourceLocation modelName, ResourceLocation opened, ResourceLocation stage0, ResourceLocation stage1, ResourceLocation stage2, ResourceLocation stage3);
  }
}
