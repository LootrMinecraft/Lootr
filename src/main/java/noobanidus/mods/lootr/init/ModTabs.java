package noobanidus.mods.lootr.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import noobanidus.mods.lootr.api.LootrAPI;

@Mod.EventBusSubscriber(modid= LootrAPI.MODID, bus= Mod.EventBusSubscriber.Bus.MOD)
public class ModTabs {
  private static final DeferredRegister<CreativeModeTab> REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, LootrAPI.MODID);

  public static final RegistryObject<CreativeModeTab> LOOTR = REGISTER.register("lootr", () -> CreativeModeTab.builder()
      .title(Component.translatable("itemGroup.lootr"))
      .icon(() -> new ItemStack(ModBlocks.TROPHY.get()))
      .displayItems((p, output) -> {
        output.accept(ModBlocks.TROPHY.get());
      }).build());

  public static void register (IEventBus bus) {
    REGISTER.register(bus);
  }
}
