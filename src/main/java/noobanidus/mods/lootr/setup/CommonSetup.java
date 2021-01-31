package noobanidus.mods.lootr.setup;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.advancement.ChestPredicate;
import noobanidus.mods.lootr.advancement.GenericTrigger;
import noobanidus.mods.lootr.init.ModMisc;

public class CommonSetup {
  public static void init(FMLCommonSetupEvent event) {
    event.enqueueWork(() -> {
      ModMisc.register();
      Lootr.CHEST_PREDICATE = CriteriaTriggers.register(new GenericTrigger<>(Lootr.CHEST_LOCATION, new ChestPredicate()));
    });
  }
}
