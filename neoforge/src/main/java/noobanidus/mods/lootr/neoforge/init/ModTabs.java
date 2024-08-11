package noobanidus.mods.lootr.neoforge.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;

public class ModTabs {
  private static final DeferredRegister<CreativeModeTab> REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, LootrAPI.MODID);

  public static final DeferredHolder<CreativeModeTab, CreativeModeTab> LOOTR = REGISTER.register("lootr", () -> CreativeModeTab.builder()
      .title(Component.translatable("itemGroup.lootr"))
      .icon(() -> new ItemStack(LootrRegistry.getTrophyItem()))
      .displayItems((p, output) -> {
        output.accept(LootrRegistry.getTrophyBlock());
      }).build());

  public static void register(IEventBus bus) {
    REGISTER.register(bus);
  }
}
