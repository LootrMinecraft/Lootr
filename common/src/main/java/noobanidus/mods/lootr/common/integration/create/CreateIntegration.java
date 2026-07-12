package noobanidus.mods.lootr.common.integration.create;

import org.jetbrains.annotations.ApiStatus;

public class CreateIntegration {
  @ApiStatus.Internal
  public static boolean SKIP_ITEM_FRAMES = false;

  public static boolean shouldSkipItems () {
    return SKIP_ITEM_FRAMES;
  }
}
