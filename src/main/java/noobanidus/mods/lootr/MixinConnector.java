package noobanidus.mods.lootr;

import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;

public class MixinConnector implements IMixinConnector {
  @Override
  public void connect() {
    Lootr.LOG.info("Initialising Mixin connector for Lootr...");
    Mixins.addConfiguration("assets/lootr/lootr.mixins.json");
  }
}
