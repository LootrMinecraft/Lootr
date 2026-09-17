package noobanidus.mods.lootr.neoforge.gametest;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;

@Mod(LootrGameTests.MODID)
public class LootrGameTests {
  public static final String MODID = "lootr_gametest";

  public LootrGameTests(IEventBus modBus) {
    modBus.addListener(this::onRegisterGameTests);
  }

  public void onRegisterGameTests(RegisterGameTestsEvent event) {
    event.register(LootrDecoratedPotGameTests.class);
  }
}
