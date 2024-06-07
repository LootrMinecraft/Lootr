package noobanidus.mods.lootr.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import noobanidus.mods.lootr.api.LootrAPI;

public class ModTabs {
  private static final DeferredRegister<CreativeModeTab> REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, LootrAPI.MODID);

  public static final DeferredHolder<CreativeModeTab, CreativeModeTab> LOOTR = REGISTER.register("lootr", () -> CreativeModeTab.builder()
      .title(Component.translatable("itemGroup.lootr"))
      .icon(() -> new ItemStack(ModBlocks.TROPHY.get()))
      .displayItems((p, output) -> {
        output.accept(ModBlocks.TROPHY.get());
      }).build());

  public static void register(IEventBus bus) {
    REGISTER.register(bus);
  }
}
