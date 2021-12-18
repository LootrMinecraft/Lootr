package noobanidus.mods.lootr.init;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import noobanidus.mods.lootr.world.processor.LootrChestProcessor;

public class ModMisc {
  public static StructureProcessorType<?> LOOTR_PROCESSOR = null;

  public static void register() {
    LOOTR_PROCESSOR = registerProcessor("lootr_chest_processor", LootrChestProcessor.CODEC);
  }

  private static <T extends StructureProcessor> StructureProcessorType<T> registerProcessor(String name, Codec<T> codec) {
    return Registry.register(Registry.STRUCTURE_PROCESSOR, name, () -> codec);
  }

  public static void init() {
  }
}
