package noobanidus.mods.lootr.init;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraft.world.gen.feature.template.StructureProcessor;
import noobanidus.mods.lootr.world.processor.LootrChestProcessor;

public class ModMisc {
  public static IStructureProcessorType<?> LOOTR_PROCESSOR = null;

  public static void register() {
    LOOTR_PROCESSOR = registerProcessor("lootr_chest_processor", LootrChestProcessor.CODEC);
  }

  private static <T extends StructureProcessor> IStructureProcessorType<T> registerProcessor(String name, Codec<T> codec) {
    return Registry.register(Registry.STRUCTURE_PROCESSOR, name, () -> codec);
  }

  public static void init() {
  }
}
